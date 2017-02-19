package com.nunez.authme;

/**
 * Created by paulnunez on 2/18/17.
 */

public class ApiConstants {
  static final String BASE_URL          = "https://www.goodreads.com";
  static final String REQUEST_TOKEN_URL = BASE_URL + "/oauth/request_token";
  static final String ACCESS_TOKEN_URL  = BASE_URL + "/oauth/access_token";
  static final String AUTHORIZE_URL     = BASE_URL + "/oauth/authorize?mobile=1";
  static final String CALLBACK_URL      = "app://libellis";
}
