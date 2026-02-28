package org.apache.cordova.firebasex;

import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import androidx.annotation.NonNull;

public class FirebasexConfigPlugin extends CordovaPlugin {

    private static final String TAG = "FirebasexConfig";

    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "pluginInitialize");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "fetch":
                if (args.length() > 0) {
                    this.fetch(callbackContext, args.getLong(0));
                } else {
                    this.fetch(callbackContext);
                }
                return true;
            case "activateFetched":
                this.activateFetched(callbackContext);
                return true;
            case "fetchAndActivate":
                this.fetchAndActivate(callbackContext);
                return true;
            case "resetRemoteConfig":
                this.resetRemoteConfig(callbackContext);
                return true;
            case "getValue":
                this.getValue(callbackContext, args.getString(0));
                return true;
            case "getInfo":
                this.getInfo(callbackContext);
                return true;
            case "getAll":
                this.getAll(callbackContext);
                return true;
            case "setConfigSettings":
                this.setConfigSettings(callbackContext, args);
                return true;
            case "setDefaults":
                this.setDefaults(callbackContext, args.getJSONObject(0));
                return true;
        }
        return false;
    }

    private void fetch(CallbackContext callbackContext) {
        handleTaskOutcome(FirebaseRemoteConfig.getInstance().fetch(), callbackContext);
    }

    private void fetch(CallbackContext callbackContext, long cacheExpirationSeconds) {
        handleTaskOutcome(FirebaseRemoteConfig.getInstance().fetch(cacheExpirationSeconds), callbackContext);
    }

    private void activateFetched(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    handleTaskOutcomeWithBooleanResult(FirebaseRemoteConfig.getInstance().activate(), callbackContext);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void fetchAndActivate(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    handleTaskOutcomeWithBooleanResult(FirebaseRemoteConfig.getInstance().fetchAndActivate(), callbackContext);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void resetRemoteConfig(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    handleTaskOutcome(FirebaseRemoteConfig.getInstance().reset(), callbackContext);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void getValue(final CallbackContext callbackContext, final String key) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    FirebaseRemoteConfigValue value = FirebaseRemoteConfig.getInstance().getValue(key);
                    callbackContext.success(value.asString());
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void getInfo(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    FirebaseRemoteConfigInfo remoteConfigInfo = FirebaseRemoteConfig.getInstance().getInfo();
                    JSONObject info = new JSONObject();
                    JSONObject settings = new JSONObject();
                    info.put("configSettings", settings);
                    info.put("fetchTimeMillis", remoteConfigInfo.getFetchTimeMillis());
                    info.put("lastFetchStatus", remoteConfigInfo.getLastFetchStatus());
                    callbackContext.success(info);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void getAll(final CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Map<String, FirebaseRemoteConfigValue> nativeValues = FirebaseRemoteConfig.getInstance().getAll();
                    JSONObject jsonValues = new JSONObject();
                    for (Map.Entry<String, FirebaseRemoteConfigValue> entry : nativeValues.entrySet()) {
                        jsonValues.put(entry.getKey(), entry.getValue().asString());
                    }
                    callbackContext.success(jsonValues);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void setConfigSettings(final CallbackContext callbackContext, final JSONArray args) throws JSONException {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    FirebaseRemoteConfigSettings.Builder settings = new FirebaseRemoteConfigSettings.Builder();
                    if (args.get(0) != null) {
                        settings.setFetchTimeoutInSeconds(args.getLong(0));
                    }
                    if (args.get(1) != null) {
                        settings.setMinimumFetchIntervalInSeconds(args.getLong(1));
                    }
                    handleTaskOutcome(FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(settings.build()), callbackContext);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void setDefaults(final CallbackContext callbackContext, final JSONObject defaults) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    handleTaskOutcome(FirebaseRemoteConfig.getInstance().setDefaultsAsync(defaultsToMap(defaults)), callbackContext);
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            }
        });
    }

    private void handleTaskOutcome(@NonNull Task task, CallbackContext callbackContext) {
        try {
            task.addOnCompleteListener((OnCompleteListener<Void>) task1 -> {
                try {
                    if (task1.isSuccessful() || task1.getException() == null) {
                        callbackContext.success();
                    } else if (task1.getException() != null) {
                        callbackContext.error(task1.getException().getMessage());
                    } else {
                        callbackContext.error("Task failed for unknown reason");
                    }
                } catch (Exception e) {
                    FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                }
            });
        } catch (Exception e) {
            FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
        }
    }

    private void handleTaskOutcomeWithBooleanResult(@NonNull Task<Boolean> task, CallbackContext callbackContext) {
        try {
            task.addOnCompleteListener(new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    try {
                        if (task.isSuccessful() || task.getException() == null) {
                            callbackContext.success(task.getResult() ? 1 : 0);
                        } else if (task.getException() != null) {
                            callbackContext.error(task.getException().getMessage());
                        } else {
                            callbackContext.error("Task failed for unknown reason");
                        }
                    } catch (Exception e) {
                        FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
                    }
                }
            });
        } catch (Exception e) {
            FirebasexCorePlugin.handleExceptionWithContext(e, callbackContext);
        }
    }

    private static Map<String, Object> defaultsToMap(JSONObject object) throws JSONException {
        final Map<String, Object> map = new HashMap<String, Object>();
        for (Iterator<String> keys = object.keys(); keys.hasNext(); ) {
            String key = keys.next();
            Object value = object.get(key);
            if (value instanceof Integer) {
                value = new Long((Integer) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                if (array.length() == 1 && array.get(0) instanceof String) {
                    value = Base64.decode(array.getString(0), Base64.DEFAULT);
                } else {
                    byte[] bytes = new byte[array.length()];
                    for (int i = 0; i < array.length(); i++)
                        bytes[i] = (byte) array.getInt(i);
                    value = bytes;
                }
            }
            map.put(key, value);
        }
        return map;
    }
}
