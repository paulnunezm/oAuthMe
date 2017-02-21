## oAuthMe
Provides a module with an Athenticator helper to be able
to perform oAuth1 authorizations with ease, granting an
easy interface and a popup dialog with a webview.

###Setup
First you should save your credentials in a safe palce as
in your ```settings.gradle``` file.

Then the activity where you want to perform the authorization
must extend ```AppCompact``` and build a
new Authenticator object with the respective parameters:

``` java
 authenticator = new com.nunez.oauthathenticator.Authenticator.Builder()
          .consumerKey(API_KEY)
          .consumerSecret(API_SECRET)
          .requestTokenUrl(REQUEST_TOKEN_URL)
          .accessTokenUrl(ACCESS_TOKEN_URL)
          .authorizeUrl(AUTHORIZE_URL)
          .callbackUrl(CALLBACK_URL)
          .listener(this) // Implemented on the activity or as an anonymous class.
          .build();
```

The activity must implement ```java Authenticator.AuthenticatorListener``` and ```java AuthDialog.RequestListener```.

Now you can call ```java authenticator.getRequestToken();``` to request the authorization url(requestToken)
needed to let the user(via browser) grant authorization to the app.

After the requestToken is received in the ```onRequestTokenReceived``` listener we can
launch the webview dialog fragment to let the user grant the autorization:

```java
@Override
public void onRequestTokenReceived(String authorizationUrl, String requestToken, String requestTokenSecret) {
    ...

    AuthDialog authDialog = AuthDialog.newInstance(authorizationUrl, CALLBACK_URL, this);
    authDialog.show(ft, "dialog");
}
```

When the user gives the authorization the ```onAuthorizationTokenReceived``` will be called
providing us with an authorization token that will be used to request the users key and secret keys
using ```authenticator.getUserSecretKeys(authToken);```

``` java
@Override
  public void onAuthorizationTokenReceived(Uri authToken) {
    // Now we can request the users keys
    authenticator.getUserSecretKeys(authToken);
  }
```

Now in the ```onUserSecretRecieved(String userKey, String userSecret)``` listener we received the
keys needed to sign each request. Be sure to store it using your preferred storing method.

And that's it.

##Contribute

To contribute just make a pull request and create a new branch with your changes.


###License
```
Copyright 2017 Paul Núñez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at \

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
