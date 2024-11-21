package com.example.gamecomplex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RankingTable_2048 extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;
    private TableLayout tableLayout;
    private TextView myScoreTextView;
    private TextView myRankingTextView;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_table2048); // XML 레이아웃 파일 이름을 사용합니다.

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        tableLayout = findViewById(R.id.tableLayout);
        myScoreTextView = findViewById(R.id.textView3);
        myRankingTextView = findViewById(R.id.textView5);
        Button backButton = findViewById(R.id.backButton2);

        // 사용자의 ID를 가져옵니다. Intent에서 가져오는 코드로 변경합니다.
        id = getIntent().getStringExtra("MEMBER_ID");
        if (id == null) {
            id = "defaultid"; // 기본 값 설정
        }

        // 순위와 점수를 표시하는 메서드를 호출합니다.
        displayRanking();

        backButton.setOnClickListener(v -> {
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }

    private void displayRanking() {
        List<UserScore> userScores = databaseAdapter.getTop10HighScores2048();
        int myHighScore = databaseAdapter.getHighScore2048(id);
        int myRanking = databaseAdapter.getUserRanking2048(id);

        for (int i = 0; i < userScores.size(); i++) {
            UserScore userScore = userScores.get(i);
            TableRow tableRow = (TableRow) tableLayout.getChildAt(i + 1); // 0번째는 헤더
            ((TextView) tableRow.getChildAt(1)).setText(userScore.getUserName());
            ((TextView) tableRow.getChildAt(2)).setText(String.valueOf(userScore.getHighScore()));
        }

        myScoreTextView.setText(String.valueOf(myHighScore));
        myRankingTextView.setText(String.valueOf(myRanking));
    }
}