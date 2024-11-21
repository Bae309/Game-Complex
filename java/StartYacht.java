package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartYacht extends AppCompatActivity {

    private Button btnStart;
    private Button btnRank;
    private Button btnEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_yacht);

        String id = getIntent().getStringExtra("MEMBER_ID");
        if (id != null) {
            Log.d("StartYacht", "MEMBER_ID: " + id);
        } else {
            Log.e("StartYacht", "MEMBER_ID is null");
        }

        btnStart = findViewById(R.id.btnstart);
        btnRank = findViewById(R.id.btnrank);
        btnEnd = findViewById(R.id.btnend);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memberId = getIntent().getStringExtra("MEMBER_ID");
                Intent intent = new Intent(StartYacht.this, PlayYacht.class);
                intent.putExtra("MEMBER_ID", memberId);
                startActivity(intent);
            }
        });

        btnRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memberId = getIntent().getStringExtra("MEMBER_ID");
                Intent intent = new Intent(StartYacht.this, RankingTable_Yacht.class);
                intent.putExtra("MEMBER_ID", memberId);
                startActivity(intent);
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartYacht.this, GameMenu.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
