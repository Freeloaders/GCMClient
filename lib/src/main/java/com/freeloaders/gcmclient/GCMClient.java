package com.freeloaders.gcmclient;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

/**
 * Created by exort on 25/2/15.
 */
public class GCMClient {
  private final String REG_ID = "REG_ID";
  private static final String APP_VERSION = "appVersion";
  private static final String TAG = "GCMClient";
  String APP_SHARED_PREFERENCE=null;
  GoogleCloudMessaging gcm;

  Context context;

  public GCMClient (Context context)
  {
    this.context = context;
    this.APP_SHARED_PREFERENCE = context.getPackageName();
  }

  @Nullable
  public String getRegId()
  {
    final SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    String registrationId = prefs.getString(REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.w(TAG, "Registration not found.");
      return "";
    }

    int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = getAppVersion(context);
    if (registeredVersion != currentVersion) {
      Log.w(TAG, "App version changed.");
      return "";
    }
    return registrationId;
  }

  public void register(final GCMCallbacks callback)
  {
    if(!TextUtils.isEmpty(getRegId()))
    {
      Log.w(TAG,"Already Registered");
    }
    else
    {
      new AsyncTask<Void, Void, String>() {
        @Override
        protected String doInBackground(Void... params) {
          String msg = "Success";
          try {
            if (gcm == null) {
              gcm = GoogleCloudMessaging.getInstance(context);
              if(gcm == null)
                return "Error: No play services";
            }
            ApplicationInfo
                ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String projectId = bundle.getString("GOOGLE_PROJECT_ID");
            Log.d(TAG,"Registering for project id :" +projectId);
            String regId = gcm.register(projectId);
            Log.d(TAG, "RegisterInBackground - regId: "+ regId);

            storeRegistrationId(context, regId);
          } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            Log.e(TAG, "Error:" + msg);
          } catch (PackageManager.NameNotFoundException e) {
            msg = e.getMessage();
            Log.e(TAG, "Error:" + msg);
          }
          return msg;
        }

        @Override
        protected void onPostExecute(String s) {
          if(s.startsWith("Error"))callback.onRegisterError();
          else {
            callback.onRegisterSuccess();
          }
          super.onPostExecute(s);
        }
      }.execute();
    }
  }

  public void deRegister()
  {
    final SharedPreferences prefs = context.getSharedPreferences(APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();
    editor.remove(REG_ID);
    editor.remove(APP_VERSION);
    editor.apply();
  }

  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      Log.d(TAG,"I never expected this! Going down, going down!" + e);
      throw new RuntimeException(e);
    }
  }

  private void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = context.getSharedPreferences(
        APP_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    int appVersion = getAppVersion(context);
    Log.i(TAG, "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(REG_ID, regId);
    editor.putInt(APP_VERSION, appVersion);
    editor.apply();
  }
}
