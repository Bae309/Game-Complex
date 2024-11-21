package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        Button Play_Yacht = findViewById(R.id.btn_yacht);
        Play_Yacht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memberId = getIntent().getStringExtra("USER_ID");
                // 다음 액티비티로 전환
                Intent intent = new Intent(GameMenu.this, StartYacht.class);
                intent.putExtra("MEMBER_ID", memberId);
                startActivity(intent);
            }
        });

        Button Play_2048 = findViewById(R.id.btn_2048);
        Play_2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memberId = getIntent().getStringExtra("USER_ID");
                // 다음 액티비티로 전환
                Intent intent = new Intent(GameMenu.this, Start2048.class);
                intent.putExtra("MEMBER_ID", memberId);
                startActivity(intent);
            }
        });

        Button MenberInfoButton = findViewById(R.id.MemberInfoButton);
        MenberInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GameMenu 액티비티로부터 전달된 사용자 아이디 가져오기
                String memberId = getIntent().getStringExtra("USER_ID");

                // 사용자 아이디가 비어있지 않은 경우에만 MemberInfo 액티비티로 전달
                if (memberId != null && !memberId.isEmpty()) {
                    Intent intent = new Intent(GameMenu.this, MemberInfo.class);
                    intent.putExtra("MEMBER_ID", memberId);
                    startActivity(intent);
                } else {
                    Toast.makeText(GameMenu.this, "사용자 아이디를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameMenu.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();  // 앱 종료
            }
        });
    }
}