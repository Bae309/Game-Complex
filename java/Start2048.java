package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Start2048 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2048);

        try {
            Button startGameButton = findViewById(R.id.Start_Button);
            Button exitButton = findViewById(R.id.GoBack_Button);
            Button ranking_2048_Button = findViewById(R.id.Ranking_Button);

            startGameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String memberId = getIntent().getStringExtra("MEMBER_ID");
                        Intent intent = new Intent(Start2048.this, Play2048.class);
                        intent.putExtra("MEMBER_ID", memberId); // 사용자 아이디 전달
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(Start2048.this, "게임을 시작하는 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });


            ranking_2048_Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String memberId = getIntent().getStringExtra("MEMBER_ID");
                        Intent intent = new Intent(Start2048.this, RankingTable_2048.class);
                        intent.putExtra("MEMBER_ID", memberId); // 사용자 아이디 전달
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(Start2048.this, "랭킹 내역을 불러오는 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(Start2048.this, "애플리케이션을 종료하는 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "UI 초기화 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
