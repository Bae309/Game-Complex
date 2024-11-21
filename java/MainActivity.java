package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 데이터베이스 어댑터 초기화 및 열기
        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        // 로그인 버튼 참조
        Button btnLogin = findViewById(R.id.btn_login);

        // 로그인 버튼 클릭 이벤트 처리
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력된 아이디와 비밀번호 가져오기
                EditText editTextID = findViewById(R.id.editTextText);
                EditText editTextPW = findViewById(R.id.editTextTextPassword);
                String id = editTextID.getText().toString().trim();
                String pw = editTextPW.getText().toString().trim();

                // 데이터베이스에서 아이디와 비밀번호 조회
                boolean isAuthenticated = databaseAdapter.authenticateUser(id, pw);

                if (isAuthenticated) {
                    // 로그인 성공 시 다음 액티비티로 전환하면서 사용자 아이디 전달
                    Intent intent = new Intent(MainActivity.this, GameMenu.class);
                    intent.putExtra("USER_ID", id);
                    startActivity(intent);
                } else {
                    // 로그인 실패 시 메시지 출력
                    Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 회원가입 버튼 참조 및 클릭 이벤트 처리
        Button btnNewMember = findViewById(R.id.btn_newMember);
        btnNewMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원가입 액티비티로 전환
                Intent intent = new Intent(MainActivity.this, NewMember.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 데이터베이스 어댑터 닫기
        databaseAdapter.close();
    }
}
