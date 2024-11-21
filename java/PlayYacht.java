package com.example.gamecomplex;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class PlayYacht extends AppCompatActivity {

    private MyDatabaseAdapter databaseAdapter;
    Button[] btnDice = new Button[12];
    Integer[] btnIds = {R.id.AceBtn, R.id.DeuceBtn, R.id.ThreeBtn, R.id.FourBtn, R.id.FiveBtn, R.id.SixBtn,
            R.id.ChoiceBtn, R.id.FourKBtn, R.id.FHouseBtn, R.id.SStraightBtn, R.id.LStraightBtn, R.id.YachtBtn};

    TextView[] textScore = new TextView[12];

    Integer[] textIds = {R.id.AceScore, R.id.DeuceScore, R.id.ThreeScore, R.id.FourScore, R.id.FiveScore,
            R.id.SixScore, R.id.ChoiceScore, R.id.FourKScore, R.id.FHouseScore, R.id.SStraightScore, R.id.LStraightScore, R.id.YachtScore};

    private ImageButton[] rollDice = new ImageButton[5];
    Integer[] rollDiceIds = {R.id.Dice1Btn, R.id.Dice2Btn, R.id.Dice3Btn,
            R.id.Dice4Btn, R.id.Dice5Btn};

    Integer[] diceIds = {R.drawable.d1, R.drawable.d2, R.drawable.d3,
            R.drawable.d4, R.drawable.d5, R.drawable.d6};

    Integer[] chkDiceIds = {R.drawable.d1chk, R.drawable.d2chk, R.drawable.d3chk,
            R.drawable.d4chk, R.drawable.d5chk, R.drawable.d6chk};

    private boolean[] ischeckedDice = {false, false, false, false, false};
    private boolean[] isclickedDice = {false, false, false, false, false, false, false, false, false, false, false, false};


    Button btnRoll;
    Button exitBtn;
    ImageButton menualBtn;

    TextView totalScore;

    TextView bonusScore;

    int i;
    int rollcnt;

    int score = 0;

    int currentTotal = 0;

    private boolean hasRolled = false;

    private boolean isBonus = false;

    private int[] diceEye = new int[5]; // 주사위 눈금

    String user_id;

    public int generateRandomNumber() {
        Random rng = new Random();
        return (rng.nextInt(6));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_yacht);

        user_id = getIntent().getStringExtra("MEMBER_ID");
        if (user_id != null) {
            Log.d("PlayYacht", "MEMBER_ID: " + user_id);
        } else {
            Log.e("PlayYacht", "USER_ID is null");
        }
        // MyDatabaseAdapter 초기화
        databaseAdapter = new MyDatabaseAdapter(this);
        databaseAdapter.open();


        rollcnt = 3;    // 주사위 카운트 초기화

        btnRoll = (Button) findViewById(R.id.RollBtn);
        totalScore = (TextView) findViewById(R.id.TotalScore);
        bonusScore = (TextView) findViewById(R.id.DiceScore);
        menualBtn = (ImageButton) findViewById(R.id.MenualBtn);
        exitBtn = (Button) findViewById(R.id.ExitBtn);

        for (i = 0; i < btnIds.length; i++) {
            btnDice[i] = (Button) findViewById(btnIds[i]);
        }

        for (i = 0; i < textScore.length; i++) {
            textScore[i] = (TextView) findViewById(textIds[i]);
        }

        for (i = 0; i < rollDice.length; i++) {
            rollDice[i] = (ImageButton) findViewById(rollDiceIds[i]);
        }

        // 게임 종료
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitConfirmationDialog();
            }
        });



        //-----게임 설명서-----
        menualBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showManualDialog();
            }
        });

        //-----주사위 굴리기-----
        btnRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rollcnt > 0) {
                    rollcnt--;  // 주사위 카운트 1
                    hasRolled = true; // 주사위를 굴렸음을 표시
                    for (i = 0; i < 5; i++) {
                        if (!ischeckedDice[i]) {  // 주사위가 고정이 되어 있지 않을 경우
                            int rng = generateRandomNumber();
                            rollDice[i].setImageResource(diceIds[rng]); // 랜덤 인덱스에 맞는 이미지를 이미지버튼에 넣기
                            rollDice[i].setTag(diceIds[rng]); // 리소스 id를 태그로 설정
                        }
                    }
                    updateScores();  // 주사위 굴린 후 점수 업데이트
                }
                if (rollcnt == 0) {
                    btnRoll.setEnabled(false);
                    btnRoll.setBackgroundColor(Color.GRAY);
                }

                btnRoll.setText("ROLL : " + rollcnt);
            }
        });

        //-----주사위 고정-----
        for (int i = 0; i < rollDice.length; i++) {
            final int index = i;
            rollDice[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasRolled) { // 주사위를 굴린 경우에만 동작
                        int currImage = -1;
                        Integer currentImageResource;
                        ischeckedDice[index] = !ischeckedDice[index];
                        if (ischeckedDice[index]) {  // 주사위가 고정이 되어 있을 경우 >> true
                            currentImageResource = (Integer) rollDice[index].getTag(); // 현재 이미지 id를 저장
                            for (int j = 0; j < diceIds.length; j++) {  // 이미지 배열만큼 반복
                                if (diceIds[j].equals(currentImageResource)) {  // 현재 이미지와 이미지 배열의 이미지가 동일할 경우
                                    currImage = j;  // 현재 이미지의 인덱스값을 저장
                                    break;  // 일치하는 이미지를 찾았으므로 루프를 종료합니다.
                                }
                            }
                            rollDice[index].setImageResource(chkDiceIds[currImage]); // 고정
                            rollDice[index].setTag(chkDiceIds[currImage]); // 리소스 ID를 태그로 설정
                        } else {    // 고정이 되어있지 않은 경우
                            currentImageResource = (Integer) rollDice[index].getTag(); // 현재 이미지 id를 저장
                            for (int j = 0; j < chkDiceIds.length; j++) {  // 이미지 배열만큼 반복
                                if (chkDiceIds[j].equals(currentImageResource)) {  // 현재 이미지와 이미지 배열의 이미지가 동일할 경우
                                    currImage = j;  // 현재 이미지의 인덱스값을 저장
                                    break;  // 일치하는 이미지를 찾았으므로 루프를 종료합니다.
                                }
                            }
                            rollDice[index].setImageResource(diceIds[currImage]); // 고정 해제
                            rollDice[index].setTag(diceIds[currImage]); // 리소스 ID를 태그로 설정
                        }
                    }
                }
            });
        }

        //-----점수 선택-----
        // 점수 입력 버튼 이벤트 처리
        for (i = 0; i < btnIds.length; i++) {
            final int index = i;
            btnDice[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasRolled) { // 주사위를 굴린 경우에만 동작
                        hasRolled = false;
                        isclickedDice[index] = true;
                        btnDice[index].setVisibility(View.INVISIBLE); // 누른 버튼 비활성화
                        for (i = 0; i < btnDice.length; i++) {
                            if (!isclickedDice[i]) {
                                textScore[i].setText("");   // 점수판 리셋
                            }
                        }
                        // 점수 계산
                        int score = calculateScore(index);
                        textScore[index].setText(String.valueOf(score));

                        // 선택한 점수를 total에 합산
                        updateTotalScore(score);
                        diceBonus();

                        // 주사위 굴리기 초기화
                        btnRoll.setEnabled(true);
                        btnRoll.setBackgroundColor(Color.parseColor("#D57676"));
                        rollcnt = 3;
                        btnRoll.setText("ROLL : " + rollcnt);
                        for (i = 0; i < rollDice.length; i++) {   // 주사위 초기화
                            rollDice[i].setImageResource(R.drawable.none);
                            ischeckedDice[i] = false;
                        }
                        hasRolled = false; // 주사위를 다시 굴리기 전까지 버튼 비활성화
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseAdapter != null) {
            databaseAdapter.close();
        }
    }

    private int calculateScore(int index) {
        int score = 0;
        switch (index) {
            case 0: // Ace
                score = calculateSumOfDice(1);
                break;
            case 1: // Deuce
                score = calculateSumOfDice(2);
                break;
            case 2: // Three
                score = calculateSumOfDice(3);
                break;
            case 3: // Four
                score = calculateSumOfDice(4);
                break;
            case 4: // Five
                score = calculateSumOfDice(5);
                break;
            case 5: // Six
                score = calculateSumOfDice(6);
                break;
            case 6: // Choice
                for (int die : diceEye) {
                    score += die;
                }
                break;
            case 7: // Four of a Kind
                score = calculateFourOfAKind();
                break;
            case 8: // Full House
                score = calculateFullHouse();
                break;
            case 9: // Small Straight
                score = calculateSmallStraight();
                break;
            case 10: // Large Straight
                score = calculateLargeStraight();
                break;
            case 11: // Yacht
                score = calculateYacht();
                break;
        }
        return score;
    }

    private int calculateSumOfDice(int face) {
        int sum = 0;
        for (int die : diceEye) {
            if (die == face) {
                sum += face;
            }
        }
        return sum;
    }

    private int calculateFourOfAKind() {
        int[] counts = new int[6];
        for (int die : diceEye) {
            counts[die - 1]++;
        }
        for (int count : counts) {
            if (count >= 4) {
                int sum = 0;
                for (int die : diceEye) {
                    sum += die;
                }
                return sum;
            }
        }
        return 0;
    }

    private int calculateFullHouse() {
        int[] counts = new int[6];
        boolean hasThree = false;
        boolean hasTwo = false;
        for (int die : diceEye) {
            counts[die - 1]++;
        }
        for (int count : counts) {
            if (count == 3) {
                hasThree = true;
            } else if (count == 2) {
                hasTwo = true;
            }
        }
        if (hasThree && hasTwo) {
            return 25;
        }
        return 0;
    }

    private int calculateSmallStraight() {
        boolean[] hasDie = new boolean[6];
        for (int die : diceEye) {
            hasDie[die - 1] = true;
        }
        if ((hasDie[0] && hasDie[1] && hasDie[2] && hasDie[3]) ||
                (hasDie[1] && hasDie[2] && hasDie[3] && hasDie[4]) ||
                (hasDie[2] && hasDie[3] && hasDie[4] && hasDie[5])) {
            return 30;
        }
        return 0;
    }

    private int calculateLargeStraight() {
        boolean[] hasDie = new boolean[6];
        for (int die : diceEye) {
            hasDie[die - 1] = true;
        }
        if ((hasDie[0] && hasDie[1] && hasDie[2] && hasDie[3] && hasDie[4]) ||
                (hasDie[1] && hasDie[2] && hasDie[3] && hasDie[4] && hasDie[5])) {
            return 40;
        }
        return 0;
    }

    private int calculateYacht() {
        int firstDie = diceEye[0];
        for (int die : diceEye) {
            if (die != firstDie) {
                return 0;
            }
        }
        return 50;
    }

    private void updateScores() {
        Integer currentImageResource;
        for (i = 0; i < diceEye.length; i++) {
            currentImageResource = (Integer) rollDice[i].getTag();
            for (int j = 0; j < diceIds.length; j++) {  // 이미지 배열만큼 반복
                if (diceIds[j].equals(currentImageResource)) {  // 현재 이미지와 이미지 배열의 이미지가 동일할 경우
                    diceEye[i] = j + 1;  // 현재 주사위 눈금의 값을 저장
                    break;
                } else if (chkDiceIds[j].equals(currentImageResource)) {  // 현재 이미지와 이미지 배열의 이미지가 동일할 경우
                    diceEye[i] = j + 1;  // 현재 주사위 눈금의 값을 저장
                    break;
                }
            }
        }

        for (int k = 0; k < textScore.length; k++) {
            score = calculateScore(k);
            if(!isclickedDice[k]) {
                textScore[k].setText(String.valueOf(score));
            }
        }
    }

    private void updateTotalScore(int selectedScore) {
        currentTotal = Integer.parseInt(totalScore.getText().toString());
        currentTotal += selectedScore;
        totalScore.setText(String.valueOf(currentTotal));

        // 모든 점수가 채워졌는지 확인
        if (isAllScoresFilled()) {
            showCompletionDialog();
        }
    }
    private void diceBonus() { // 보너스 점수 체크
        int total = 0;
        int bonusThreshold = 63; // 보너스 조건 63점
        int bonusPoints = 35; // 보너스 점수

        // 첫 번째 섹션의 점수를 합산
        for (int i = 0; i < 6; i++) {
            String scoreText = textScore[i].getText().toString();
            if (!scoreText.isEmpty()) {
                total += Integer.parseInt(scoreText);
            }
        }
        bonusScore.setText(total + "/63");

        // 보너스 조건 체크
        if (total >= bonusThreshold) {
            if(!isBonus) { // 보너스 점수를 받지 않았다면
                isBonus = true;
                if (!bonusScore.getText().toString().contains("+35")) {
                    currentTotal += bonusPoints;
                    bonusScore.setText(total + "/63 +35"); // 보너스 표시
                }
            }
        }

        // 전체 점수 업데이트
        totalScore.setText(String.valueOf(currentTotal));
    }

    private boolean isAllScoresFilled() {
        for (int i = 0; i < textScore.length; i++) {
            if (textScore[i].getText().toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void showCompletionDialog() {
        //SharedPreferences sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        //String userId = sharedPref.getString("user_id", "default_user_id");

        int myScore = databaseAdapter.getHighScoreYacht(user_id);
        Log.d("PlayYachtMYScore", "Score: " + myScore);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게임 종료");
        builder.setMessage("모든 점수가 채워졌습니다. 다시 하시겠습니까? 아니면 메인 화면으로 나가시겠습니까?\n" +
                "당신의 최고 점수 : " + myScore + "\n" + "당신의 점수 : " + currentTotal);
        builder.setPositiveButton("다시 하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateScoreIfHigher(); // 점수 업데이트
                // 게임을 다시 시작하는 로직
                resetGame();
            }
        });
        builder.setNegativeButton("메인 화면으로", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                updateScoreIfHigher(); // 점수 업데이트
                Intent intent = new Intent(PlayYacht.this, StartYacht.class);

                intent.putExtra("MEMBER_ID", user_id);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
        builder.setCancelable(false); // 사용자가 다이얼로그 밖을 클릭해도 다이얼로그가 닫히지 않도록 설정
        builder.show();
    }

    private void resetGame() {
        // 게임 상태 초기화 로직
        for (int i = 0; i < textScore.length; i++) {
            textScore[i].setText("");
            isclickedDice[i] = false;
            btnDice[i].setVisibility(View.VISIBLE); // 점수 버튼을 다시 보이게 설정
        }
        currentTotal = 0;
        totalScore.setText(String.valueOf(currentTotal));
        bonusScore.setText("0/63");

        rollcnt = 3;
        btnRoll.setText("ROLL : " + rollcnt);
        btnRoll.setEnabled(true);
        btnRoll.setBackgroundColor(Color.parseColor("#D57676"));

        for (int i = 0; i < rollDice.length; i++) {
            rollDice[i].setImageResource(R.drawable.none);
            ischeckedDice[i] = false;
        }
    }

    private void showManualDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게임 설명서");
        builder.setMessage("Yacht 게임의 룰 설명서:\n\n" +
                "1. 게임 목적: 주사위를 굴려 가능한 높은 점수를 얻는 것입니다. 각 턴에서 5개의 주사위를 굴리고, 다양한 조합으로 점수를 기록합니다.\n\n" +
                "2. 게임 방법:\n" +
                "   - 각 턴에서 주사위를 최대 3번 굴릴 수 있습니다.\n\n" +
                "   - 원하는 주사위를 선택하여 고정할 수 있습니다. 고정된 주사위는 다시 굴리지 않습니다.\n\n" +
                "   - 3번의 굴림 후, 점수 카테고리에 해당하는 점수를 선택하여 기록합니다.\n\n" +
                "3. 점수 카테고리:\n" +
                "   - Aces (1점): 주사위에서 1의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Deuces (2점): 주사위에서 2의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Threes (3점): 주사위에서 3의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Fours (4점): 주사위에서 4의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Fives (5점): 주사위에서 5의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Sixes (6점): 주사위에서 6의 눈이 나온 개수만큼 점수를 얻습니다.\n\n" +
                "   - Choice: 주사위의 모든 눈의 합계를 점수로 얻습니다.\n\n" +
                "   - Four of a Kind: 같은 눈 4개가 나온 경우, 주사위의 모든 눈의 합계를 점수로 얻습니다.\n\n" +
                "   - Full House: 같은 눈 3개와 같은 눈 2개가 나온 경우, 25점을 얻습니다.\n\n" +
                "   - Small Straight: 연속된 숫자 4개가 나온 경우, 30점을 얻습니다.\n\n" +
                "   - Large Straight: 연속된 숫자 5개가 나온 경우, 40점을 얻습니다.\n\n" +
                "   - Yacht: 같은 눈 5개가 나온 경우, 50점을 얻습니다.\n\n" +
                "4. 보너스 점수:\n" +
                "   - Aces, Deuces, Threes, Fours, Fives, Sixes 카테고리에서 총 63점을 넘으면 35점의 보너스를 얻습니다.\n\n" +
                "5. 게임 종료:\n" +
                "   - 모든 점수 카테고리에 점수를 기록하면 게임이 종료됩니다.\n\n" +
                "   - 가장 높은 점수를 얻은 플레이어가 승리합니다.\n\n" +
                "즐거운 게임 되세요!");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게임 종료");
        builder.setMessage("게임을 종료하시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateScoreIfHigher(); // 점수 업데이트
                Intent intent = new Intent(PlayYacht.this, StartYacht.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });
        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(true);
        builder.show();
    }

    private void updateScoreIfHigher() {
        if (databaseAdapter != null) {
            int newScore = currentTotal;
            databaseAdapter.updateHighScoreYacht(user_id, newScore);
        }
    }
}
