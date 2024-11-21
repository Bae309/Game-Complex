package com.example.gamecomplex;

import static com.example.gamecomplex.R.id.progressBar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingView extends AppCompatActivity {
    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_view); // 레이아웃 파일 설정

        mProgress = findViewById(progressBar);
        mProgress.setMax(100);
        mProgress.setProgress(0);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mProgressStatus < 100) {
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgressStatus = i++;

                    mProgress.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });
                }

                // ProgressBar가 100% 채워졌을 때 MainActivity로 전환
                if (mProgressStatus >= 100) {
                    Intent intent = new Intent(LoadingView.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // 현재 액티비티 종료
                }
            }
        }).start();
    }
}