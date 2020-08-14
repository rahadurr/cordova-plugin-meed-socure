package com.meedbankingclub.cordova.socure;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;

public class SocureHelper {
    private static SharedPreferences.Editor editor;
    private static final String PLUGIN_PREFERENCES = "com.meedbankingclub.cordova.socure";

    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;

    public static String[] permissionsMapper(JSONArray permissions) throws JSONException {
        String[] permissionList = new String[permissions.length()];
        for (int i = 0; i < permissions.length(); i++) {
            String permission = permissions.getString(i);
            if (permission.equals("camera")) {
                permissionList[i] = PERMISSION_CAMERA;
            } else if (permission.equals("location")) {
                permissionList[i] = PERMISSION_ACCESS_FINE_LOCATION;
            }
        }
        return permissionList;
    }


    public static boolean shouldAskedPermission(CordovaPlugin plugin, String[] permissions) {
        boolean askedPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (!PermissionHelper.hasPermission(plugin, permission)) {
                    askedPermission = true;
                }
            }
        }
        return askedPermission;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void checkPermissions(CordovaInterface cordova, String permission, PermissionListener permissionListener) {
        SharedPreferences sharedPreferences = cordova.getContext().getSharedPreferences(PLUGIN_PREFERENCES, Context.MODE_PRIVATE);

        if (cordova.getActivity().shouldShowRequestPermissionRationale(permission)) {
            permissionListener.onPermissionDeniedOnce();
        } else {
            boolean firstTimeAsked = sharedPreferences.getBoolean(permission, true);
            if (firstTimeAsked) {
                if (editor == null) editor = sharedPreferences.edit();
                editor.putBoolean(permission, false);
                editor.apply();
                editor = null;
            } else {
                permissionListener.onPermissionDeniedAlways();
            }
        }
    }

    public static Bitmap byteToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String base64ImageData(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

}

interface PermissionListener {
    void onPermissionDeniedOnce();
    void onPermissionDeniedAlways();
}
