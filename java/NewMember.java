package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NewMember extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_member);

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        // 회원가입 완료 버튼 참조
        Button btnComplete = findViewById(R.id.Complete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewMember();
            }
        });

        // 아이디 중복 확인 버튼 참조
        Button btnCheckIdOverlap = findViewById(R.id.Check_IdOverlap);
        btnCheckIdOverlap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIdOverlap();
            }
        });
    }

    private void registerNewMember() {
        EditText editTextName = findViewById(R.id.enter_name);
        EditText editTextNickname = findViewById(R.id.enter_Nickname);
        EditText editTextId = findViewById(R.id.enter_Id);
        EditText editTextPassword = findViewById(R.id.enter_Passwd);
        EditText editTextPasswordCheck = findViewById(R.id.Passwd_Check);

        String name = editTextName.getText().toString().trim();
        String nickname = editTextNickname.getText().toString().trim();
        String id = editTextId.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordCheck = editTextPasswordCheck.getText().toString().trim();

        if (name.isEmpty() || nickname.isEmpty() || id.isEmpty() || password.isEmpty() || passwordCheck.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordCheck)) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseAdapter.checkIdExists(id)) {
            Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseAdapter.insertData(id, password, name, nickname, 0, 0);
        if (result > 0) {
            Toast.makeText(this, "회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(NewMember.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "회원 가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIdOverlap() {
        EditText editTextId = findViewById(R.id.enter_Id);
        String id = editTextId.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseAdapter.checkIdExists(id)) {
            Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }
}
