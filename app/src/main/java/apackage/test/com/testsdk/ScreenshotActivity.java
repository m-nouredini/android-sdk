package apackage.test.com.testsdk;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arioclub.android.sdk.auth.AuthService;
import com.arioclub.android.sdk.common.ConnectionResult;
import com.arioclub.android.sdk.common.api.ArioGameApiClient;
import com.arioclub.android.sdk.common.api.ResultCallback;
import com.arioclub.android.sdk.common.api.Status;
import com.arioclub.android.sdk.games.Games;

/**
 * Created by USER
 * on 8/12/2017.
 */

public class ScreenshotActivity extends AppCompatActivity implements
        View.OnClickListener,
        ArioGameApiClient.OnConnectionFailedListener,
        ArioGameApiClient.ConnectionCallbacks{

    ArioGameApiClient apiClient;

    private static final int REQUEST_LOGIN = 101;
    private static final int REQUEST_SCREENSHOT = 106;
    private static final String TAG = ScreenshotActivity.class.getSimpleName();

    EditText etCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screenshot);

        apiClient  = new ArioGameApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        .build();

        etCaption = (EditText) findViewById(R.id.et_caption);

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
        (findViewById(R.id.bt_intent_screenshot)).setOnClickListener(this);
        (findViewById(R.id.bt_service_screenshot)).setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_intent_screenshot:
                    startActivityForResult(Games.Screenshot.getScreenshotIntent(apiClient, takeScreenShot(), etCaption.getText().toString())
                            ,  REQUEST_SCREENSHOT);
                break;

            case R.id.bt_service_screenshot:
                if (etCaption.getText().toString().equals("")) {
                    Toast.makeText(this, "write caption!", Toast.LENGTH_SHORT).show();
                } else {
                    Games.Screenshot.postScreenShot(apiClient, takeScreenShot(), etCaption.getText().toString())
                                                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d(TAG, "now user be returned from screenshot activity");
                            if (status.isSuccess())
                                Toast.makeText(ScreenshotActivity.this, "posting image successful", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(ScreenshotActivity.this, "posting image unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCREENSHOT) {
            Log.d(TAG, "now user be returned from screenshot activity");
        } else if (requestCode == REQUEST_LOGIN) {
            apiClient.connect();
        }
    }

    private Bitmap takeScreenShot() {
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        return bitmap;
    }
}
