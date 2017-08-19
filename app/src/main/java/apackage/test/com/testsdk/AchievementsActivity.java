package apackage.test.com.testsdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arioclub.android.sdk.common.ConnectionResult;
import com.arioclub.android.sdk.common.api.ArioGameApiClient;
import com.arioclub.android.sdk.common.api.ResultCallback;
import com.arioclub.android.sdk.games.Games;
import com.arioclub.android.sdk.games.achievement.Achievement;
import com.arioclub.android.sdk.games.achievement.Achievements;

/**
 * Created by USER
 * on 8/8/2017.
 */

public class AchievementsActivity extends AppCompatActivity implements
        View.OnClickListener,
        ArioGameApiClient.OnConnectionFailedListener,
        ArioGameApiClient.ConnectionCallbacks{

    ArioGameApiClient apiClient;

    Button ach1;
    Button ach2;
    Button ach3;
    Button btUnlock;
    Button btIncrement;
    Button btShowAchievements;
    TextView tvState;
    ProgressBar pbLoading;

    String flagAchievementId = "";
    private static final int REQUEST_LOGIN = 101;
    private static final int REQUEST_ACHIEVEMENTS = 103;
    final String TAG = AchievementsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_achievement);

        apiClient  = new ArioGameApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        .build();

        ach1 = (Button) findViewById(R.id.bt_ach1);
        ach2 = (Button) findViewById(R.id.bt_ach2);
        ach3 = (Button) findViewById(R.id.bt_ach3);
        btUnlock = (Button) findViewById(R.id.bt_unlock);
        btIncrement = (Button) findViewById(R.id.bt_inc);
        btShowAchievements = (Button) findViewById(R.id.bt_all_ach);
        tvState = (TextView) findViewById(R.id.tv_achievement_state);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_ach1:
                enableButton(ach1);
                flagAchievementId = getString(R.string.achievement_id_1);
                updateState(flagAchievementId);
                break;
            case R.id.bt_ach2:
                enableButton(ach2);
                flagAchievementId = getString(R.string.achievement_id_2);
                updateState(flagAchievementId);
                break;
            case R.id.bt_ach3:
                enableButton(ach3);
                flagAchievementId = getString(R.string.achievement_id_3);
                updateState(flagAchievementId);
                break;
            case R.id.bt_unlock:
                Games.Achievements.unlock(apiClient, flagAchievementId);
                break;
            case R.id.bt_inc:
                Games.Achievements.increment(apiClient, flagAchievementId, 1);
                break;
            case R.id.bt_all_ach:
                startActivityForResult(Games.Achievements.getAchievementsIntent(apiClient), REQUEST_ACHIEVEMENTS);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        ach1.setOnClickListener(this);
        ach2.setOnClickListener(this);
        ach3.setOnClickListener(this);
        btUnlock.setOnClickListener(this);
        btIncrement.setOnClickListener(this);
        btShowAchievements.setOnClickListener(this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //if user not singin in ario apiclient can not connect then this method called
        //we check if failed signin cause of not login so show login activity to login user then
        //attempt to connect
        try {
            if (connectionResult.getStatus().getErrorCode() == ConnectionResult.SIGN_IN_FAILED)
                startActivityForResult(Games.GamesMetadata.getLoginIntent(this), REQUEST_LOGIN);
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "ario application not installed");
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ACHIEVEMENTS) {
            Log.d(TAG, "now user be returned from achievement activity");
        } else if (requestCode == REQUEST_LOGIN) {
            apiClient.connect();
        }
    }

    private void enableButton(Button button) {
        button.setBackgroundResource(android.R.drawable.button_onoff_indicator_on);
        if (!button.equals(ach1)) {
            ach1.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
        }
        if (!button.equals(ach2)) {
            ach2.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
        }
        if (!button.equals(ach3)) {
            ach3.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
        }
    }

    private void updateState(final String id) {
        pbLoading.setVisibility(View.VISIBLE);
        tvState.setVisibility(View.INVISIBLE);
        Games.Achievements.load(apiClient, true).setResultCallback(new ResultCallback<Achievements.LoadAchievementsResult>() {
            @Override
            public void onResult(@NonNull Achievements.LoadAchievementsResult loadAchievementsResult) {
                pbLoading.setVisibility(View.INVISIBLE);
                tvState.setVisibility(View.VISIBLE);
                boolean isAchievementFound = false;
                for (int i=0 ; i<loadAchievementsResult.getAchievements().getCount(); i++){
                    Achievement item = loadAchievementsResult.getAchievements().get(i);
                    if (item.getAchievementId().equals(id)) {
                        isAchievementFound = true;
                        tvState.setText("state: " + (item.getState()==0 ? "unlock" : "lock"));
                        tvState.append("\nname: " + item.getName());
                        tvState.append("\ndescription: " + item.getDescription());
                        tvState.append("\nvalue: " + item.getCurrentSteps());
                        tvState.append("\nsumValue: " + item.getTotalSteps());
                        tvState.append("\nxp: " + item.getXpValue());
                    }
                }
                if (!isAchievementFound) {
                    tvState.setText(R.string.not_found);
                }
            }
        });
    }
}
