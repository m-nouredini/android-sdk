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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.arioclub.android.sdk.common.ConnectionResult;
import com.arioclub.android.sdk.common.api.ArioGameApiClient;
import com.arioclub.android.sdk.common.api.ResultCallback;
import com.arioclub.android.sdk.games.Games;
import com.arioclub.android.sdk.games.leaderboard.Leaderboard;
import com.arioclub.android.sdk.games.leaderboard.LeaderboardScore;
import com.arioclub.android.sdk.games.leaderboard.LeaderboardVariant;
import com.arioclub.android.sdk.games.leaderboard.Leaderboards;

/**
 * Created by USER
 * on 8/13/2017.
 */

public class LeaderboardActivity extends AppCompatActivity implements
        View.OnClickListener,
        ArioGameApiClient.OnConnectionFailedListener,
        ArioGameApiClient.ConnectionCallbacks{

    EditText etScore;
    TextView tvInfo;
    TextView tvTopScore;
    Button btSubmit;
    Button btShowLeaderboard;

    ArioGameApiClient apiClient;

    private static final int REQUEST_LOGIN = 101;
    private static final int REQUEST_LEADERBOARD = 103;
    final String TAG = AchievementsActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_leaderboard);

        apiClient = new ArioGameApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        etScore = (EditText) findViewById(R.id.et_score);
        tvInfo = (TextView) findViewById(R.id.tv_info);
        tvTopScore = (TextView) findViewById(R.id.tv_top_scores);
        btSubmit = (Button) findViewById(R.id.bt_submit_score);
        btShowLeaderboard = (Button) findViewById(R.id.bt_show_leaderboard);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit_score:
                if (etScore.getText().toString().equals(""))
                    Toast.makeText(this, "score is null!", Toast.LENGTH_SHORT).show();
                else
                    Games.Leaderboards.submitScore(apiClient, getString(R.string.leaderboard_id), Integer.valueOf(etScore.getText().toString()));
                break;

            case R.id.bt_show_leaderboard:
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(apiClient, getString(R.string.leaderboard_id)), REQUEST_LEADERBOARD);
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        initLeaderboardInfo();
        initTopScore();
        btSubmit.setOnClickListener(this);
        btShowLeaderboard.setOnClickListener(this);
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
        if (requestCode == REQUEST_LEADERBOARD) {
            Log.d(TAG, "now user be returned from leaderboard activity");
        } else if (requestCode == REQUEST_LOGIN) {
            apiClient.connect();
        }
    }

    private void initLeaderboardInfo() {
        Games.Leaderboards.loadLeaderboardMetadata(apiClient, getString(R.string.leaderboard_id), true)
                .setResultCallback(new ResultCallback<Leaderboards.LeaderboardMetadataResult>() {
                    @Override
                    public void onResult(@NonNull Leaderboards.LeaderboardMetadataResult leaderboardMetadataResult) {
                        try {
                            Leaderboard temp = leaderboardMetadataResult.getLeaderboards().get(0);
                            tvInfo.setText("name: "+ temp.getDisplayName() + "\n id: "+ temp.getLeaderboardId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void initTopScore() {
        Games.Leaderboards.loadTopScores(apiClient, getString(R.string.leaderboard_id), LeaderboardVariant.TIME_SPAN_ALL_TIME
                , LeaderboardVariant.COLLECTION_PUBLIC, 10).setResultCallback(new ResultCallback<Leaderboards.LoadScoresResult>() {
            @Override
            public void onResult(@NonNull Leaderboards.LoadScoresResult loadScoresResult) {
                try {
                    tvTopScore.setText(R.string.top_10_score);
                    for (int i = 0; i < loadScoresResult.getScores().getCount(); i++) {
                        LeaderboardScore score = loadScoresResult.getScores().get(i);
                        tvTopScore.append("\n" + score.getDisplayRank() + "-" + score.getScoreHolderDisplayName() + ": " + score.getDisplayScore());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
