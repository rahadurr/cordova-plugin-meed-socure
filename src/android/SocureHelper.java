package com.meedbankingclub.cordova.socure;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import com.socure.idplus.model.BarcodeData;
import com.socure.idplus.model.MrzData;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    public static JSONObject mapMRZData(MrzData mrzData) throws JSONException {
        JSONObject json = new JSONObject();
        if (mrzData != null) {
            json.put("code", "");
            json.put("format", "");
            json.put("surName", mrzData.getSurName());
            json.put("firstName", mrzData.getFirstName());
            json.put("issuingCountry", mrzData.getIssuingCountry());
            json.put("nationality", mrzData.getNationality());
            json.put("sex", "");
            json.put("dob", mrzData.getDob());
            json.put("documentNumber", mrzData.getDocumentNumber());
            json.put("expirationDate", mrzData.getExpirationDate());
        } else  {
            json.put("code", "");
            json.put("format", "");
            json.put("surName", "");
            json.put("firstName", "");
            json.put("issuingCountry", "");
            json.put("nationality", "");
            json.put("sex", "");
            json.put("dob", "");
            json.put("documentNumber", "");
            json.put("expirationDate", "");
        }
        return json;
    }

    public static JSONObject mapBarcodeData(BarcodeData barcodeData) throws JSONException {
        JSONObject json = new JSONObject();
        if (barcodeData != null) {
            json.put("firstName", barcodeData.getFirstName());
            json.put("lastName", "");
            json.put("middleName", "");
            json.put("fullName", barcodeData.getFullName());
            json.put("dob", barcodeData.getDOB());
            json.put("address", barcodeData.getAddress());
            json.put("city", barcodeData.getCity());
            json.put("state", barcodeData.getState());
            json.put("postalCode", barcodeData.getPostalCode());
            json.put("documentNumber", barcodeData.getDocumentNumber());
            json.put("issueDate", barcodeData.getIssueDate());
            json.put("expirationDate", barcodeData.getExpirationDate());
        } else  {
            json.put("firstName", "");
            json.put("lastName", "");
            json.put("middleName", "");
            json.put("fullName", "");
            json.put("dob", "");
            json.put("address", "");
            json.put("city", "");
            json.put("state", "");
            json.put("postalCode", "");
            json.put("documentNumber", "");
            json.put("issueDate", "");
            json.put("expirationDate", "");
        }
        return json;
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
