package com.arioclub.sdk.example;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arioclub.android.basegameutils.BaseGameActivity;
import com.arioclub.android.sdk.games.Games;

import java.util.Random;


public class GuessNumGame extends BaseGameActivity implements View.OnClickListener {

    private Button button0, button1, button2, button3, button4, button5,
            button6, button7, button8, button9, buttonAgain;
    private int number;
    private Random rand;
    private TextView info;
    private int numGuesses = 0;
    private int countCorrectGuess = 0;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.test_game);

        enableDebugLog(true);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.show_achievements).setOnClickListener(this);
        findViewById(R.id.show_leaderboard).setOnClickListener(this);
        findViewById(R.id.comment).setOnClickListener(this);
        findViewById(R.id.show_gamepage).setOnClickListener(this);
        findViewById(R.id.show_developer_games).setOnClickListener(this);
        findViewById(R.id.help).setOnClickListener(this);
        findViewById(R.id.screenshot).setOnClickListener(this);

        button0 = (Button) findViewById(R.id.btn0);
        button1 = (Button) findViewById(R.id.btn1);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);
        button4 = (Button) findViewById(R.id.btn4);
        button5 = (Button) findViewById(R.id.btn5);
        button6 = (Button) findViewById(R.id.btn6);
        button7 = (Button) findViewById(R.id.btn7);
        button8 = (Button) findViewById(R.id.btn8);
        button9 = (Button) findViewById(R.id.btn9);
        buttonAgain = (Button) findViewById(R.id.btnAgain);


        info = (TextView) findViewById(R.id.guess_text);

        rand = new Random();
        number = rand.nextInt(10);
    }


    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }

    @Override
    public void onSignInSucceeded() {
        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            beginUserInitiatedSignIn();
        } else if (view.getId() == R.id.sign_out_button) {
            signOut();
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
        } else if (view.getId() == R.id.show_achievements) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(getApiClient()), 1);
        } else if (view.getId() == R.id.show_leaderboard) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(getApiClient()), 1);
        } else if (view.getId() == R.id.show_gamepage) {
            try {
                startActivity(Games.GamesMetadata.getGamePageIntent(/*package name of the game*/"com.arioclub.android", this));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.show_developer_games) {
            try {
                startActivity(Games.GamesMetadata.getDeveloperGamesIntent(/*developer id*/"13", this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.comment) {
            try {
                startActivity(Games.GamesMetadata.getCommentOnGameIntent(/*package name of the game*/"com.arioclub.android", this));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        } else if (view.getId() == R.id.help) {
            Toast.makeText(this, number + "", Toast.LENGTH_SHORT).show();
        } else if (view.getId() == R.id.screenshot) {
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            String postCaption = "یه بازی خوب و سرگرم کننده";
            Games.Screenshot.postScreenShot(getApiClient(), bitmap, postCaption);
        } else {
            btnPressed(view);
        }
    }

    private void disableNumbers() {
        button0.setEnabled(false);
        button0.setTextColor(Color.parseColor("#ff000033"));
        button1.setEnabled(false);
        button1.setTextColor(Color.parseColor("#ff000033"));
        button2.setEnabled(false);
        button2.setTextColor(Color.parseColor("#ff000033"));
        button3.setEnabled(false);
        button3.setTextColor(Color.parseColor("#ff000033"));
        button4.setEnabled(false);
        button4.setTextColor(Color.parseColor("#ff000033"));
        button5.setEnabled(false);
        button5.setTextColor(Color.parseColor("#ff000033"));
        button6.setEnabled(false);
        button6.setTextColor(Color.parseColor("#ff000033"));
        button7.setEnabled(false);
        button7.setTextColor(Color.parseColor("#ff000033"));
        button8.setEnabled(false);
        button8.setTextColor(Color.parseColor("#ff000033"));
        button9.setEnabled(false);
        button9.setTextColor(Color.parseColor("#ff000033"));
        buttonAgain.setEnabled(true);
        buttonAgain.setTextColor(Color.parseColor("#ff000033"));
    }

    private void enableNumbers() {
        button0.setEnabled(true);
        button0.setTextColor(Color.WHITE);
        button1.setEnabled(true);
        button1.setTextColor(Color.WHITE);
        button2.setEnabled(true);
        button2.setTextColor(Color.WHITE);
        button3.setEnabled(true);
        button3.setTextColor(Color.WHITE);
        button4.setEnabled(true);
        button4.setTextColor(Color.WHITE);
        button5.setEnabled(true);
        button5.setTextColor(Color.WHITE);
        button6.setEnabled(true);
        button6.setTextColor(Color.WHITE);
        button7.setEnabled(true);
        button7.setTextColor(Color.WHITE);
        button8.setEnabled(true);
        button8.setTextColor(Color.WHITE);
        button9.setEnabled(true);
        button9.setTextColor(Color.WHITE);
        buttonAgain.setEnabled(false);
        buttonAgain.setTextColor(Color.parseColor("#ffffff00"));
    }

    public void btnPressed(View v) {
        int btn = Integer.parseInt(v.getTag().toString());
        if (btn < 0) {
            //again btn
            numGuesses = 0;
            number = rand.nextInt(10);
            enableNumbers();
            info.setTextColor(Color.BLACK);
            info.setText(R.string.guess);
        } else {
            //number button
            countCorrectGuess++;
            numGuesses++;
            if (btn == number) {
                info.setTextColor(Color.GREEN);
                info.setText(getString(R.string.correct_the_answer_was_number_d, number));
                if (getApiClient().isConnected()) {
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_id_1), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_id_2), 1);
                    Games.Achievements.increment(getApiClient(), getString(R.string.achievement_id_3), 1);
                    Games.Leaderboards.submitScore(getApiClient(), getString(R.string.leaderboard_id), countCorrectGuess);
                }
                disableNumbers();
            } else if (numGuesses == 2) {
                info.setTextColor(Color.RED);
                info.setText(getString(R.string.wrong_the_answer_was_number_d, number));
                disableNumbers();
            } else {
                info.setTextColor(Color.GRAY);
                info.setText(R.string.wrong_guess_another_num);
            }
        }
    }


}
