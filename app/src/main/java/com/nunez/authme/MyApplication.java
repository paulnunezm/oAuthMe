package com.nunez.authme;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by paulnunez on 2/20/17.
 */

public class MyApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    // Create an InitializerBuilder
    Stetho.InitializerBuilder initializerBuilder =
        Stetho.newInitializerBuilder(this);

// Enable Chrome DevTools
    initializerBuilder.enableWebKitInspector(
        Stetho.defaultInspectorModulesProvider(this)
    );

//// Enable command line interface
//    initializerBuilder.enableDumpapp(
//        Stetho.defaultDumperPluginsProvider(context)
//    );

// Use the InitializerBuilder to generate an Initializer
    Stetho.Initializer initializer = initializerBuilder.build();

// Initialize Stetho with the Initializer
    Stetho.initialize(initializer);
  }
}
