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

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.nunez.authme.model.GoodreadsResponse;
import com.nunez.oauthathenticator.AuthDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

import static com.nunez.authme.ApiConstants.CALLBACK_URL;

public class MainActivity extends  AppCompatActivity implements com.nunez.oauthathenticator.Authenticator.AuthenticatorListener,
    AuthDialog.RequestListener{

  private static final String TAG = "MainActivity";

  private TextView textView;
  private WebView  webview;
  private String mUserkey;
  private String mUserSecret;
  private String mRequestToken;
  private String mRequestTokenSecret;

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
          .callbackUrl(BuildConfig.CALLBACK_URL)
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
  public void onRequestTokenReceived(String authorizationUrl, String requestToken, String requestTokenSecret) {
    mRequestToken = requestToken;
    mRequestTokenSecret = requestTokenSecret;

    android.support.v4.app.FragmentTransaction ft   = getSupportFragmentManager().beginTransaction();
    android.support.v4.app.Fragment            prev = getSupportFragmentManager().findFragmentByTag("dialog");
    if (prev != null) {
      ft.remove(prev);
    }
    ft.addToBackStack(null);

    // Create and show the dialog.
    AuthDialog authDialog = AuthDialog.newInstance(authorizationUrl, CALLBACK_URL, this);
    authDialog.show(ft, "dialog");
  }

  @Override
  public void onUserSecretRecieved(String userKey, String userSecret) {
    Log.d(TAG, "onUserSecretRecieved() called with: userKey = [" + userKey + "], userSecret = [" + userSecret + "]");
    mUserkey = userKey;
    mUserSecret = userSecret;
//    requestUserDetails();
    requestUserDetailsWithRetrofit();
  }

  @Override
  public void onAuthorizationTokenReceived(Uri authToken) {
    Log.d(TAG, "onAuthorizationTokenReceived() called with: authToken = [" + authToken + "]");
    textView.setText("Authenticated");
    // Now we can request the users
    authenticator.getUserSecretKeys(authToken);
  }

  public void requestUserDetailsWithRetrofit(){
    Retrofit signedRetrofit= new SignedRetrofit(mUserkey, mUserSecret)
        .getSignedRetrofit();

    GoodreadsApi                      api  = signedRetrofit.create(GoodreadsApi.class);
    retrofit2.Call<GoodreadsResponse> call = api.getUserNameAndId();
    call.enqueue(new retrofit2.Callback<GoodreadsResponse>() {
      @Override
      public void onResponse(retrofit2.Call<GoodreadsResponse> call, retrofit2.Response<GoodreadsResponse> response) {
        if(response.isSuccessful()){
          Log.d(TAG, "onResponse: user:"+ response.body().getUser().getName());
        }
      }

      @Override
      public void onFailure(retrofit2.Call<GoodreadsResponse> call, Throwable t) {
        Log.e(TAG, "onFailure: "+t.toString(), t);
      }
    });
  }

  public void requestUserDetails(){
    OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(BuildConfig.GOODREADS_API_KEY,
        BuildConfig.GOODREADS_API_SECRET);

    consumer.setTokenWithSecret(mUserkey, mUserSecret); // this are the values that needs to be saved

    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new StethoInterceptor())
        .addInterceptor(new SigningInterceptor(consumer))
        .build();

    Request request = new Request.Builder()
        .url("https://www.goodreads.com/api/auth_user")
        .build();

    Request signedRequest;
//      signedRequest = (Request) consumer.sign(request).unwrap();
      client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
          Log.e(TAG, "onFailure: call failed");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
          Log.i(TAG, "onResponse: " + response.body().string());
        }
      });
  }
}
