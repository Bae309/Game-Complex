package com.example.gamecomplex;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ModifyInfo extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;
    private String memberId;

    private EditText nameEditText;
    private EditText nicknameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        memberId = getIntent().getStringExtra("MEMBER_ID");
        if (memberId == null) {
            Toast.makeText(this, "회원 ID가 전달되지 않았습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        nameEditText = findViewById(R.id.nameEditText);
        nicknameEditText = findViewById(R.id.nicknameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Cursor cursor = databaseAdapter.getMemberInfo(memberId);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow("NickName"));
            nameEditText.setText(name);
            nicknameEditText.setText(nickname);
        }
        cursor.close();

        Button saveButton = findViewById(R.id.btn_complete);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = nameEditText.getText().toString().trim();
                String newNickname = nicknameEditText.getText().toString().trim();
                String newPassword = passwordEditText.getText().toString().trim();

                if (newName.isEmpty() || newNickname.isEmpty()) {
                    Toast.makeText(ModifyInfo.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isUpdated = databaseAdapter.updateMemberInfoWithPassword(memberId, newName, newNickname, newPassword);
                    if (isUpdated) {
                        Toast.makeText(ModifyInfo.this, "회원 정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ModifyInfo.this, "회원 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button cancelButton = findViewById(R.id.btn_back);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
