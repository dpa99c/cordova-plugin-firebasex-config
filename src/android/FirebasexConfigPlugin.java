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

/**
 * Cordova plugin for Firebase Remote Config on Android.
 *
 * <p>Provides fetching, activating, and reading Remote Config values,
 * setting defaults, configuring fetch settings, and resetting config.
 *
 * @see <a href="https://firebase.google.com/docs/remote-config">Firebase Remote Config</a>
 */
public class FirebasexConfigPlugin extends CordovaPlugin {

    /** Log tag for all messages from this plugin. */
    private static final String TAG = "FirebasexConfig";

    /** Initialises the plugin. */
    @Override
    protected void pluginInitialize() {
        Log.d(TAG, "pluginInitialize");
    }

    /**
     * Dispatches Cordova actions to plugin methods.
     *
     * <p>Supported actions: fetch, activateFetched, fetchAndActivate, resetRemoteConfig,
     * getValue, getInfo, getAll, setConfigSettings, setDefaults.
     */
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

    /** Fetches Remote Config values using the default cache expiration. */
    private void fetch(CallbackContext callbackContext) {
        handleTaskOutcome(FirebaseRemoteConfig.getInstance().fetch(), callbackContext);
    }

    /**
     * Fetches Remote Config values with a custom cache expiration.
     *
     * @param callbackContext the Cordova callback
     * @param cacheExpirationSeconds cache duration in seconds
     */
    private void fetch(CallbackContext callbackContext, long cacheExpirationSeconds) {
        handleTaskOutcome(FirebaseRemoteConfig.getInstance().fetch(cacheExpirationSeconds), callbackContext);
    }

    /** Activates the most recently fetched configs. Returns boolean result. */
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

    /** Fetches and activates Remote Config values in a single operation. Returns boolean result. */
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

    /** Resets all Remote Config values back to their defaults. */
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

    /**
     * Gets a single Remote Config value by key, returned as a string.
     *
     * @param callbackContext the Cordova callback
     * @param key the parameter key
     */
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

    /**
     * Gets metadata about the Remote Config instance.
     * Returns fetchTimeMillis, lastFetchStatus, and configSettings.
     */
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

    /** Gets all Remote Config parameter values as a JSON object of key-string pairs. */
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

    /**
     * Sets the fetch timeout and minimum fetch interval.
     *
     * @param callbackContext the Cordova callback
     * @param args args[0]: fetchTimeout, args[1]: minimumFetchInterval (both in seconds)
     */
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

    /**
     * Sets default values for Remote Config parameters.
     *
     * @param callbackContext the Cordova callback
     * @param defaults JSON object of key-value default pairs
     */
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

    /**
     * Handles a Task outcome by sending a Cordova success or error result.
     * Success sends with no data; failure sends the exception message.
     */
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

    /**
     * Handles a Task<Boolean> outcome by sending the boolean result (1/0)
     * as a Cordova success, or the exception message as an error.
     */
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

    /**
     * Converts a JSON object to a Map for use as Remote Config defaults.
     *
     * <p>Handles type conversion: Integer values are widened to Long,
     * JSONArray values are converted to byte arrays (either Base64-decoded
     * from a single string element or raw byte values).
     *
     * @param object the JSON defaults object
     * @return the converted map
     * @throws JSONException if parsing fails
     */
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
