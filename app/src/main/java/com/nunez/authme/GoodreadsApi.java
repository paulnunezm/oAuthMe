package com.nunez.authme;

import com.nunez.authme.model.GoodreadsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by paulnunez on 2/20/17.
 */

public interface GoodreadsApi {
 @GET("api/auth_user")
  Call<GoodreadsResponse> getUserNameAndId();
}
