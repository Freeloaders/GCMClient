package com.freeloaders.gcmclient;

/**
 * Created by exort on 25/2/15.
 */
public class GCMClient {
  String getRegId()
  {
    return null;
  }
  boolean register(GCMCallbacks callbacks)
  {
    callbacks.onRegisterSuccess();
    return false;
  }

  boolean deRegister()
  {
    return false;
  }
}
