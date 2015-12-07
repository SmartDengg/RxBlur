package com.joker.blurapplication;

import android.app.Application;
import android.os.StrictMode;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Created by Joker on 2015/8/24.
 */
public class MyApplication extends Application {

  private static MyApplication instance;
  private RefWatcher refWatcher;

  public static MyApplication get() {
    return instance;
  }

  public RefWatcher getRefWatcher() {
    return MyApplication.get().refWatcher;
  }

  @Override public void onCreate() {
    super.onCreate();

    instance = (MyApplication) getApplicationContext();

    MyApplication.this.enabledStrictMode();
    refWatcher = LeakCanary.install(this);
  }

  private void enabledStrictMode() {
    if (SDK_INT >= GINGERBREAD) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
          .detectAll()  //
          .penaltyLog() //
          .penaltyDeath() //
          .build());
    }
  }
}
