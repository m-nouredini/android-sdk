/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arioclub.android.basegameutils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.arioclub.android.sdk.common.api.ArioGameApiClient;

/**
 * Example base class for games. This implementation takes care of setting up
 * the API client object and managing its lifecycle. Subclasses only need to
 * override the @link{#onSignInSucceeded} and @link{#onSignInFailed} abstract
 * methods. To initiate the sign-in flow when the user clicks the sign-in
 * button, subclasses should call @link{#beginUserInitiatedSignIn}. By default,
 * this class only instantiates the ArioApiClient object.
 *
 */
public abstract class BaseGameActivity extends FragmentActivity implements
        GameHelper.GameHelperListener {

    // The game helper object. This class is mainly a wrapper around this object.
    protected GameHelper mHelper;

    // We don't support clients in Ario,
    // We just keep them to support Google API.
    public static final int CLIENT_GAMES = GameHelper.CLIENT_GAMES;
    public static final int CLIENT_PLUS = GameHelper.CLIENT_PLUS;
    public static final int CLIENT_ALL = GameHelper.CLIENT_ALL;

    // By default, that's just the games client.
    protected int mRequestedClients = CLIENT_GAMES;

    private final static String TAG = "BaseGameActivity";
    protected boolean mDebugLog = false;

    /** Constructs a BaseGameActivity with default client (GamesClient). */
    protected BaseGameActivity() {
        super();
    }

    /**
     * Constructs a BaseGameActivity with the game client, ignoring the requestedClients param.
     * @param requestedClients ignored parameter.
     */
    protected BaseGameActivity(int requestedClients) {
        super();
        Log.w(TAG, "requestedClients param ignored, only game client is available.");
        setRequestedClients(CLIENT_GAMES);
    }

    /**
     * Sets the requested clients to CLIENT_GAMES Ignoring the requestedClients arg,
     * this method is here to support Google API.
     *
     * @param requestedClients this parameter is ignored and has no effect.
     */
    protected void setRequestedClients(int requestedClients) {
        Log.w(TAG, "requestedClients param ignored, only game client is available.");
        mRequestedClients = CLIENT_GAMES;
    }

    public GameHelper getGameHelper() {
        if (mHelper == null) {
            mHelper = new GameHelper(this, mRequestedClients);
            mHelper.enableDebugLog(mDebugLog);
        }
        return mHelper;
    }

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        if (mHelper == null) {
            getGameHelper();
        }
        mHelper.setup(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHelper.onStop();
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        mHelper.onActivityResult(request, response, data);
    }

    protected ArioGameApiClient getApiClient() {
        return mHelper.getApiClient();
    }

    protected boolean isSignedIn() {
        return mHelper.isSignedIn();
    }

    protected void beginUserInitiatedSignIn() {
        mHelper.beginUserInitiatedSignIn();
    }

    protected void signOut() {
        mHelper.signOut();
    }

    protected void showAlert(String message) {
        mHelper.makeSimpleDialog(message).show();
    }

    protected void showAlert(String title, String message) {
        mHelper.makeSimpleDialog(title, message).show();
    }

    protected void enableDebugLog(boolean enabled) {
        mDebugLog = true;
        if (mHelper != null) {
            mHelper.enableDebugLog(enabled);
        }
    }

    @Deprecated
    protected void enableDebugLog(boolean enabled, String tag) {
        Log.w(TAG, "BaseGameActivity.enabledDebugLog(bool,String) is " +
                "deprecated. Use enableDebugLog(boolean)");
        enableDebugLog(enabled);
    }

    protected String getInvitationId() {
        return mHelper.getInvitationId();
    }

    protected void reconnectClient() {
        mHelper.reconnectClient();
    }

    protected boolean hasSignInError() {
        return mHelper.hasSignInError();
    }

    protected GameHelper.SignInFailureReason getSignInError() {
        return mHelper.getSignInError();
    }
}
