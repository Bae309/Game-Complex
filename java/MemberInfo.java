package com.example.gamecomplex;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

public class MemberInfo extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;
    private String memberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        // 이전 액티비티에서 전달된 memberId를 가져옴
        memberId = getIntent().getStringExtra("MEMBER_ID");

        // memberId가 null인지 확인
        if (memberId == null) {
            Toast.makeText(this, "회원 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish(); // 액티비티 종료
            return;
        }

        TextView nameText = findViewById(R.id.nameText);
        TextView nicknameText = findViewById(R.id.nicknameText);
        TextView idText = findViewById(R.id.idText);

        Cursor cursor = databaseAdapter.getMemberInfo(memberId);
        if (cursor.moveToFirst()) {
            // 열 이름이 정확한지 확인
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow("NickName"));
            String id = cursor.getString(cursor.getColumnIndexOrThrow("ID"));

            nameText.setText(name);
            nicknameText.setText(nickname);
            idText.setText(id);
        }
        cursor.close();

        Button modifyButton = findViewById(R.id.ModifyButton);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 비밀번호 확인을 위한 팝업창 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(MemberInfo.this);
                builder.setTitle("비밀번호 확인");

                // 팝업창에 비밀번호 입력을 위한 EditText 추가
                final EditText inputPassword = new EditText(MemberInfo.this);
                inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(inputPassword);

                // 팝업창의 확인 버튼 설정
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredPassword = inputPassword.getText().toString().trim();

                        // 데이터베이스에서 해당 회원의 비밀번호 가져오기
                        String passwordFromDB = databaseAdapter.getPasswordForMember(memberId);

                        // 입력된 비밀번호와 데이터베이스의 비밀번호 비교
                        if (enteredPassword.equals(passwordFromDB)) {
                            // 비밀번호가 일치하는 경우 수정 액티비티로 전환
                            Intent intent = new Intent(MemberInfo.this, ModifyInfo.class);
                            intent.putExtra("MEMBER_ID", memberId); // "MEMBER_ID" 전달
                            startActivity(intent);
                            finish();
                        } else {
                            // 비밀번호가 일치하지 않는 경우 메시지 출력
                            Toast.makeText(MemberInfo.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 팝업창의 취소 버튼 설정
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 팝업창 표시
                builder.show();
            }
        });

        Button deleteButton = findViewById(R.id.DeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAdapter.deleteMember(memberId);
                Intent intent = new Intent(MemberInfo.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemberInfo.this, GameMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }
}
