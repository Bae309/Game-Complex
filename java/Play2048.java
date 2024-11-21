package com.example.gamecomplex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Play2048 extends AppCompatActivity {
    private Button[][] buttons = new Button[4][4];
    private int[][] gameBoard = new int[4][4];
    private Random random = new Random();
    private int score;
    private MyDatabaseAdapter databaseAdapter;
    private String id;

    private TextView scoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play2048);

        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();

        // 사용자의 ID를 가져옵니다. Intent에서 가져오는 코드로 변경합니다.
        id = getIntent().getStringExtra("MEMBER_ID");
        if (id == null) {
            id = "defaultid"; // 기본 값 설정
        }

        try {
            initViews();
            startGame();
        } catch (Exception e) {
            Toast.makeText(this, "초기화 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }

    private void initViews() {
        // Button 배열 초기화
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
            }
        }

        scoreTextView = findViewById(R.id.score_text);

        Button restartButton = findViewById(R.id.button_restart);
        restartButton.setOnClickListener(v -> startGame());

        ImageButton pauseButton = findViewById(R.id.button_stop);
        pauseButton.setOnClickListener(v -> new AlertDialog.Builder(Play2048.this)
                .setTitle("게임 일시정지")
                .setMessage("원하는 작업을 선택하세요.")
                .setPositiveButton("계속하기", (dialog, which) -> {
                    // Do nothing, just close the dialog to continue the game
                })
                .setNeutralButton("메뉴로 나가기", (dialog, which) -> {
                    try {
                        Intent intent = new Intent(Play2048.this, Start2048.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Close the Play2048 activity
                    } catch (Exception e) {
                        Toast.makeText(Play2048.this, "메뉴로 나가기 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("게임 종료하기", (dialog, which) -> {
                    try {
                        finishAffinity(); // Close the Play2048 activity to exit the game
                    } catch (Exception e) {
                        Toast.makeText(Play2048.this, "게임 종료 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .show());

        setSwipeListeners();
    }

    private void setSwipeListeners() {
        View.OnTouchListener swipeListener = new OnSwipeTouchListener(Play2048.this) {
            @Override
            public void onSwipeTop() {
                moveUp();
            }

            @Override
            public void onSwipeRight() {
                moveRight();
            }

            @Override
            public void onSwipeLeft() {
                moveLeft();
            }

            @Override
            public void onSwipeBottom() {
                moveDown();
            }
        };

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j].setOnTouchListener(swipeListener);
            }
        }
    }

    private void startGame() {
        score = 0;
        updateScore();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                gameBoard[i][j] = 0;
            }
        }
        addRandomTile();
        addRandomTile();
        updateUI();
    }

    private void updateScore() {
        scoreTextView.setText("Score: " + score);
    }

    private void addRandomTile() {
        int x, y;
        int emptyCount = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameBoard[i][j] == 0) {
                    emptyCount++;
                }
            }
        }
        if (emptyCount == 0) {
            return; // 모든 타일이 채워져 있으면 타일 추가하지 않음
        }
        do {
            x = random.nextInt(4);
            y = random.nextInt(4);
        } while (gameBoard[x][y] != 0);
        gameBoard[x][y] = random.nextInt(10) == 0 ? 4 : 2;
    }

    private void updateUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameBoard[i][j] == 0) {
                    buttons[i][j].setText("");
                    buttons[i][j].setBackgroundColor(getColorForTile(0));
                } else {
                    buttons[i][j].setText(String.valueOf(gameBoard[i][j]));
                    buttons[i][j].setBackgroundColor(getColorForTile(gameBoard[i][j]));
                    buttons[i][j].setTextSize(30);
                }
            }
        }
        updateScore();
        checkGameOver();
    }

    private void moveUp() {
        boolean moved = false;
        try {
            for (int j = 0; j < 4; j++) {
                for (int i = 1; i < 4; i++) {
                    if (gameBoard[i][j] != 0) {
                        int k = i;
                        while (k > 0 && gameBoard[k - 1][j] == 0) {
                            gameBoard[k - 1][j] = gameBoard[k][j];
                            gameBoard[k][j] = 0;
                            k--;
                            moved = true;
                        }
                        if (k > 0 && gameBoard[k - 1][j] == gameBoard[k][j]) {
                            gameBoard[k - 1][j] *= 2;
                            score += gameBoard[k - 1][j];
                            gameBoard[k][j] = 0;
                            moved = true;
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "위로 이동 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (moved) {
            addRandomTile();
            updateUI();
        }
    }

    private void moveDown() {
        boolean moved = false;
        try {
            for (int j = 0; j < 4; j++) {
                for (int i = 2; i >= 0; i--) {
                    if (gameBoard[i][j] != 0) {
                        int k = i;
                        while (k < 3 && gameBoard[k + 1][j] == 0) {
                            gameBoard[k + 1][j] = gameBoard[k][j];
                            gameBoard[k][j] = 0;
                            k++;
                            moved = true;
                        }
                        if (k < 3 && gameBoard[k + 1][j] == gameBoard[k][j]) {
                            gameBoard[k + 1][j] *= 2;
                            score += gameBoard[k + 1][j];
                            gameBoard[k][j] = 0;
                            moved = true;
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "아래로 이동 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (moved) {
            addRandomTile();
            updateUI();
        }
    }

    private void moveLeft() {
        boolean moved = false;
        try {
            for (int i = 0; i < 4; i++) {
                for (int j = 1; j < 4; j++) {
                    if (gameBoard[i][j] != 0) {
                        int k = j;
                        while (k > 0 && gameBoard[i][k - 1] == 0) {
                            gameBoard[i][k - 1] = gameBoard[i][k];
                            gameBoard[i][k] = 0;
                            k--;
                            moved = true;
                        }
                        if (k > 0 && gameBoard[i][k - 1] == gameBoard[i][k]) {
                            gameBoard[i][k - 1] *= 2;
                            score += gameBoard[i][k - 1];
                            gameBoard[i][k] = 0;
                            moved = true;
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "왼쪽으로 이동 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (moved) {
            addRandomTile();
            updateUI();
        }
    }

    private void moveRight() {
        boolean moved = false;
        try {
            for (int i = 0; i < 4; i++) {
                for (int j = 2; j >= 0; j--) {
                    if (gameBoard[i][j] != 0) {
                        int k = j;
                        while (k < 3 && gameBoard[i][k + 1] == 0) {
                            gameBoard[i][k + 1] = gameBoard[i][k];
                            gameBoard[i][k] = 0;
                            k++;
                            moved = true;
                        }
                        if (k < 3 && gameBoard[i][k + 1] == gameBoard[i][k]) {
                            gameBoard[i][k + 1] *= 2;
                            score += gameBoard[i][k + 1];
                            gameBoard[i][k] = 0;
                            moved = true;
                        }
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Toast.makeText(this, "오른쪽으로 이동 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (moved) {
            addRandomTile();
            updateUI();
        }
    }

    private int getColorForTile(int value) {
        switch (value) {
            case 0:
                return 0xFFCDC1B4;
            case 2:
                return 0xFFEEE4DA;
            case 4:
                return 0xFFEDE0C8;
            case 8:
                return 0xFFF2B179;
            case 16:
                return 0xFFF59563;
            case 32:
                return 0xFFF67C5F;
            case 64:
                return 0xFFF65E3B;
            case 128:
                return 0xFFEDCF72;
            case 256:
                return 0xFFEDCC61;
            case 512:
                return 0xFFEDC850;
            case 1024:
                return 0xFFEDC53F;
            case 2048:
                return 0xFFEDC22E;
            case 4096:
                return 0xFF3D3A33;
            case 8192:
                return 0xFF3D3A34;
            case 16384:
                return 0xFF3D3A35;
            case 32768:
                return 0xFF3D3A36;
            case 65536:
                return 0xFF3D3A37;
            case 131072:
                return 0xFF3D3A38;
            case 262144:
                return 0xFF3D3A39;
            case 524288:
                return 0xFF3D3A3A;
            case 1048576:
                return 0xFF3D3A3B;
            default:
                return 0xFF3C3A32;
        }
    }

    private void checkGameOver() {
        boolean gameOver = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (gameBoard[i][j] == 0) {
                    gameOver = false;
                    break;
                }
                if (i > 0 && gameBoard[i][j] == gameBoard[i - 1][j]) {
                    gameOver = false;
                    break;
                }
                if (i < 3 && gameBoard[i][j] == gameBoard[i + 1][j]) {
                    gameOver = false;
                    break;
                }
                if (j > 0 && gameBoard[i][j] == gameBoard[i][j - 1]) {
                    gameOver = false;
                    break;
                }
                if (j < 3 && gameBoard[i][j] == gameBoard[i][j + 1]) {
                    gameOver = false;
                    break;
                }
            }
        }
        if (gameOver) {
            int currentHighScore = databaseAdapter.getHighScore2048(id);
            if (score > currentHighScore) {
                databaseAdapter.updateHighScore2048(id, score); // 최고 점수 업데이트
            }

            AlertDialog gameOverDialog = new AlertDialog.Builder(this)
                    .setTitle("Game Over")
                    .setMessage("No more moves available. Game Over!\nYour score: " + score)
                    .setPositiveButton("다시하기", (dialog, which) -> {
                        try {
                            startGame();
                        } catch (Exception e) {
                            Toast.makeText(this, "게임 다시 시작 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("나가기", (dialog, which) -> {
                        try {
                            Intent intent = new Intent(Play2048.this, Start2048.class);
                            intent.putExtra("MEMBER_ID", id); // 사용자 아이디 전달
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            Toast.makeText(this, "게임 종료 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .setCancelable(false) // 이 부분이 팝업 창 외부 터치시 닫힘을 방지합니다.
                    .create();

            gameOverDialog.show();
        }
    }
}

