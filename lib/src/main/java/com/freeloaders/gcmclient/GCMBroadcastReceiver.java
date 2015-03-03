package com.freeloaders.gcmclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by USER on 18-01-2015.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

  private static final String TAG = "GCMBroadcastReceiver";

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.e("chetan", "BroadcastReceiver");
    try {
      ApplicationInfo ai =
          context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      Bundle bundle = ai.metaData;
      String intentServiceName = bundle.getString("GCM_RECEIVE_INTENT_SERVICE");
      Log.d(TAG,"IntentServiceName : " + intentServiceName );
      ComponentName comp = new ComponentName(context.getPackageName(),intentServiceName);
      startWakefulService(context, (intent.setComponent(comp)));
      setResultCode(Activity.RESULT_OK);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }
}