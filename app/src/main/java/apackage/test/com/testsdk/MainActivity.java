package apackage.test.com.testsdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.arioclub.android.sdk.auth.AuthService;
import com.arioclub.android.sdk.common.ConnectionResult;
import com.arioclub.android.sdk.common.api.ArioGameApiClient;
import com.arioclub.android.sdk.common.api.ResultCallback;
import com.arioclub.android.sdk.common.api.Status;
import com.arioclub.android.sdk.games.Games;

public class MainActivity extends AppCompatActivity implements
        ArioGameApiClient.OnConnectionFailedListener,
        ArioGameApiClient.ConnectionCallbacks,
        View.OnClickListener{

    ArioGameApiClient apiClient;

    final String TAG = MainActivity.class.getSimpleName();
    final int LOGIN_REQ = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiClient = new ArioGameApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        .build();

        findViewById(R.id.bt_achievement).setOnClickListener(this);
        findViewById(R.id.bt_leaderboard).setOnClickListener(this);
        findViewById(R.id.bt_screenshot).setOnClickListener(this);
        findViewById(R.id.bt_gamepage).setOnClickListener(this);
        findViewById(R.id.bt_comment).setOnClickListener(this);
        findViewById(R.id.bt_developer_games).setOnClickListener(this);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_guss_num).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_achievement:
                startActivity(new Intent(this, AchievementsActivity.class));
                break;

            case R.id.bt_leaderboard:
                startActivity(new Intent(this, LeaderboardActivity.class));
                break;

            case R.id.bt_screenshot:
                startActivity(new Intent(this, ScreenshotActivity.class));
                break;

            case R.id.bt_gamepage:
                try {
                    // first param is package name of game
                    startActivity(Games.GamesMetadata.getGamePageIntent("com.rovio.gold",this));
                } catch (PackageManager.NameNotFoundException e) {
                    // if ario application is not installed on device, this block will be running
                    e.printStackTrace();
                    Toast.makeText(this, "ario application not installed", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bt_comment:
                try {
                    // first param is package name of game
                    startActivity(Games.GamesMetadata.getCommentOnGameIntent("com.rovio.gold",this));
                } catch (PackageManager.NameNotFoundException e) {
                    // if ario application is not installed on device, this block will be running
                    e.printStackTrace();
                    Toast.makeText(this, "ario application not installed", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bt_developer_games:
                try {
                    // first param is developer id
                    startActivity(Games.GamesMetadata.getDeveloperGamesIntent("22",this));
                } catch (PackageManager.NameNotFoundException e) {
                    // if ario application is not installed on device, this block will be running
                    e.printStackTrace();
                    Toast.makeText(this, "ario application not installed", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.bt_login:
                //first we are check user's login status with "isLogin" method as below shown
                new AuthService().isLogin(apiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // if status.isSuccess() is false that mean user not login into ario application
                        // so we can show login activity to the user
                        if (!status.isSuccess()) {
                            try {
                                startActivityForResult(Games.GamesMetadata.getLoginIntent(MainActivity.this), LOGIN_REQ);
                            } catch (PackageManager.NameNotFoundException e) {
                                // if ario application is not installed on device, this block will be running
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "ario application not installed", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                break;

            case R.id.bt_guss_num:
                startActivity(new Intent(this, GuessNumGame.class));
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

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQ) {
            // when login's activity shown to the user if user could success in login into ario
            // then RESULT_OK return to this activity so we can sure that user login into ario was success so we call
            // apiClient.connect();
            if (resultCode == RESULT_OK) {
                apiClient.connect();
            }
        }
    }
}
