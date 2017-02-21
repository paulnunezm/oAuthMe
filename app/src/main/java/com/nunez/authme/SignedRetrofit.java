package com.nunez.authme;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;


/**
 * Handles the retrofit signing process.
 */
public class SignedRetrofit {
  private static String mUserKey;
  private static String mUserSecret;

  public SignedRetrofit(String userkey, String userSecret) {
    mUserKey = userkey;
    mUserSecret = userSecret;
  }

  public Retrofit getSignedRetrofit() {
    OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(BuildConfig.GOODREADS_API_KEY,
        BuildConfig.GOODREADS_API_SECRET);

    consumer.setTokenWithSecret(mUserKey, mUserSecret); // this are the values that needs to be saved

    OkHttpClient client = new OkHttpClient.Builder()
        .addInterceptor(new SigningInterceptor(consumer))
        .build();

    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .client(client)
        .build();

    return retrofit;
  }

}
