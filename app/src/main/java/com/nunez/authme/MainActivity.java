package com.nunez.authme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.nunez.oauthathenticator.AuthDialog;

import static com.nunez.authme.ApiConstants.CALLBACK_URL;

public class MainActivity extends  AppCompatActivity implements com.nunez.oauthathenticator.Authenticator.AuthenticatorListener,
    AuthDialog.RequestListener{

  private static final String TAG = "MainActivity";

  private TextView textView;
  private WebView  webview;

  private com.nunez.oauthathenticator.Authenticator authenticator;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    textView = (TextView) findViewById(R.id.status);

    Intent  intent = getIntent();
    Boolean isAuth = intent.getBooleanExtra("auth", false);

    if (isAuth) {
      textView.setText("is Authed!!!");
      // Save data and request user data.
    } else {
      // Be sad.
      textView.setText("Not yet :(");
    }

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        checkIfAuthenticated();
      }
    });
  }


  public void checkIfAuthenticated() {
    Log.d(TAG, "checkIfAuthenticated() called");

    // Instanciamos los settings para saber si ya hemos sido auth
    SharedPreferences preferences = getSharedPreferences("oauthPrefs", 0);

    if (!preferences.getBoolean("authenticated", false)) {
      textView.setText("Authenticating");

      authenticator = new com.nunez.oauthathenticator.Authenticator.Builder()
          .consumerKey(BuildConfig.GOODREADS_API_KEY)
          .consumerSecret(BuildConfig.GOODREADS_API_SECRET)
          .requestTokenUrl(ApiConstants.REQUEST_TOKEN_URL)
          .accessTokenUrl(ApiConstants.ACCESS_TOKEN_URL)
          .authorizeUrl(ApiConstants.AUTHORIZE_URL)
          .callbackUrl(CALLBACK_URL)
          .listener(this)
          .build();

      authenticator.getRequestToken();

    } else {
      textView.setText("authenticated");
      textView.setVisibility(View.VISIBLE);
      webview.setVisibility(View.GONE);
    }
  }

  @Override
  public void onRequestTokenReceived(String requestToken) {

    // authDialog.show() will take care of adding the fragment
    // in a transaction.  We also want to remove any currently showing
    // dialog, so make our own transaction and take care of that here.
    android.support.v4.app.FragmentTransaction ft   = getSupportFragmentManager().beginTransaction();
    android.support.v4.app.Fragment            prev = getSupportFragmentManager().findFragmentByTag("dialog");
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);

    // Create and show the dialog.
    AuthDialog authDialog = AuthDialog.newInstance(requestToken, CALLBACK_URL, this);
    authDialog.show(ft, "dialog");
  }

  @Override
  public void onUserSecretRecieved(String userKey, String userSecret) {
    Log.d(TAG, "onUserSecretRecieved() called with: userKey = [" + userKey + "], userSecret = [" + userSecret + "]");
  }

  @Override
  public void onAuthorizationTokenReceived(Uri authToken) {
    Log.d(TAG, "onAuthorizationTokenReceived() called with: authToken = [" + authToken + "]");
    textView.setText("Authenticated");
    // Now we can request the users
    authenticator.getUserSecretKeys(authToken);
  }
}
