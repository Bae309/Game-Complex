package com.example.gamecomplex;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RankingTable_Yacht extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;
    private TableLayout tableLayout;
    private TextView myScoreTextView;
    private TextView myRankingTextView;

    private String userid;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_table_yacht);

        tableLayout = findViewById(R.id.tableLayout);
        myScoreTextView = findViewById(R.id.textView3);
        myRankingTextView = findViewById(R.id.textView5);
        backButton = findViewById(R.id.backButton2);

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        userid = getIntent().getStringExtra("MEMBER_ID");
        if (userid == null) {
            userid = "defaultid"; // 기본 값 설정
        }

        displayRanking();
        backButton.setOnClickListener(v -> finish()); // 현재 액티비티를 종료하고 이전 액티비티로 돌아감
    }

    private int getMyScoreYacht(String userid) {

        int score = databaseAdapter.getHighScoreYacht(userid);

        return score;
    }

    private int getUserRankingYacht(String userId) {
        return databaseAdapter.getUserRankingYacht(userId);
    }

    private List<UserScore> getTop10HighScoresYacht() {
        return databaseAdapter.getTop10HighScoresYacht();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }

    private void displayRanking() {
        List<UserScore> userScores = databaseAdapter.getTop10HighScoresYacht();
        int myHighScore = databaseAdapter.getHighScoreYacht(userid);
        int myRanking = databaseAdapter.getUserRankingYacht(userid);

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

