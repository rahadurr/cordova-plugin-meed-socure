package com.meedbankingclub.cordova.socure;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import com.socure.idplus.SDKAppDataPublic;
import com.socure.idplus.model.ScanResult;
import com.socure.idplus.model.SelfieScanResult;
import com.socure.idplus.scanner.selfie.SelfieActivity;
import com.socure.idplus.scanner.license.LicenseScannerActivity;
import com.socure.idplus.scanner.passport.PassportScannerActivity;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PermissionHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This class echoes a string called from JavaScript.
 */
public class Socure extends CordovaPlugin {

    private static final String ACTION_CHECK_PERMISSIONS = "checkPermissions";
    private static final String ACTION_SCAN_LICENSE = "scanLicense";
    private static final String ACTION_SCAN_PASSPORT = "scanPassport";
    private static final String ACTION_SCAN_SELFIE = "scanSelfie";
    private static final String ACTION_OPEN_SETTING = "openSetting";

    private static final int PERMISSION_STATUS_GRANTED = 100;
    private static final int PERMISSION_STATUS_DENIED_ONCE = 200;
    private static final int PERMISSION_STATUS_DENIED_ALWAYS = 300;

    private static final int REQUEST_CAPTURE_LICENSE = 500;
    private static final int REQUEST_CAPTURE_PASSPORT = 600;
    private static final int REQUEST_CAPTURE_SELFIE = 700;


    private int permissionStatus = PERMISSION_STATUS_GRANTED;

    private CordovaInterface cordova;
    private CallbackContext callbackContext;
    private Bundle pluginState;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        this.cordova = cordova;
        this.pluginState = new Bundle();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        switch (action) {
            case ACTION_CHECK_PERMISSIONS:
                try {
                    this.checkPermissions(args, callbackContext);
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                return true;
            case Socure.ACTION_SCAN_LICENSE:
                try {
                    this.scanLicense();
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                return true;
            case ACTION_SCAN_PASSPORT:
                try {
                    this.scanPassport();
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                return true;
            case ACTION_SCAN_SELFIE:
                try {
                    this.scanSelfie();
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                return true;
            case ACTION_OPEN_SETTING:
                try {
                    this.openSetting(callbackContext);
                } catch (Exception ex) {
                    callbackContext.error(ex.getMessage());
                }
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            int r = grantResults[i];
            if (r == PackageManager.PERMISSION_DENIED) {
                SocureHelper.checkPermissions(this.cordova, permissions[i], new PermissionListener() {
                    @Override
                    public void onPermissionDeniedOnce() {
                        permissionStatus = PERMISSION_STATUS_DENIED_ONCE;
                    }

                    @Override
                    public void onPermissionDeniedAlways() {
                        permissionStatus = PERMISSION_STATUS_DENIED_ALWAYS;
                    }
                });
            }
        }
        if (permissionStatus != PERMISSION_STATUS_GRANTED) {
            callbackContext.success(permissionStatus);
        } else {
            callbackContext.success(PERMISSION_STATUS_GRANTED);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            switch (requestCode) {
                case REQUEST_CAPTURE_LICENSE:
                    if (resultCode == Activity.RESULT_OK) {

                        ScanResult result = SDKAppDataPublic.INSTANCE.getSuccessfulScanningResult();
                        this.callbackContext.success(
                                new JSONObject().put("licenseFrontImage", SocureHelper.base64ImageData(result.getLicenseFrontImage()))
                                        .put("licenseBackImage", SocureHelper.base64ImageData(result.getLicenseBackImage())));
                    } else {
                        this.callbackContext.success(
                                new JSONObject().put("licenseFrontImage", "")
                                        .put("licenseBackImage", ""));
                    }
                    break;
                case REQUEST_CAPTURE_PASSPORT:
                    if (resultCode == Activity.RESULT_OK) {

                        ScanResult result = SDKAppDataPublic.INSTANCE.getSuccessfulScanningResult();
                        byte[] passportImage = result.getPassportImage();
                        this.callbackContext.success(
                                new JSONObject().put("passportImage", SocureHelper.base64ImageData(passportImage)));


                    } else {
                        this.callbackContext.success(
                                new JSONObject().put("passportImage", ""));
                    }
                    break;
                case REQUEST_CAPTURE_SELFIE:
                    if (resultCode == Activity.RESULT_OK) {
                        SelfieScanResult result = SDKAppDataPublic.INSTANCE.getSelfieScanResult();
                        byte[] selfieImage = result.getImageData();
                        this.callbackContext.success(
                                new JSONObject().put("selfieImage", SocureHelper.base64ImageData(selfieImage)));
                    } else {
                        this.callbackContext.success(
                                new JSONObject().put("selfieImage", ""));
                    }
                    break;
            }
        } catch (Exception ex) {
            this.callbackContext.error(
                    new JSONObject());
        }

    }

    @Override
    public Bundle onSaveInstanceState() {
        LOG.d("MEED[Socure](onRestoreStateForActivityResult)", this.pluginState.toString());
        return this.pluginState;

    }

    @Override
    public void onRestoreStateForActivityResult(Bundle state, CallbackContext callbackContext) {
        super.onRestoreStateForActivityResult(state, callbackContext);
        LOG.d("MEED[Socure](onRestoreStateForActivityResult)", state.toString());
        this.pluginState = state;
        this.callbackContext = callbackContext;
    }


    private void checkPermissions(JSONArray options, CallbackContext callbackContext) throws JSONException {
        JSONArray permissions = options.getJSONArray(0);
        String[] requestedPermissions = SocureHelper.permissionsMapper(permissions);

        if (SocureHelper.shouldAskedPermission(this, requestedPermissions)) {
            PermissionHelper.requestPermissions(this, 100, requestedPermissions);
        } else {
            callbackContext.success(PERMISSION_STATUS_GRANTED);
        }

    }


    private void scanLicense() throws JSONException {
        Intent scanLicenseIntent = new Intent(this.cordova.getActivity(), LicenseScannerActivity.class);
        this.cordova.startActivityForResult(this, scanLicenseIntent, REQUEST_CAPTURE_LICENSE);
    }

    private void scanPassport() throws JSONException {
        Intent scanPassportIntent = new Intent(this.cordova.getActivity(), PassportScannerActivity.class);
        this.cordova.startActivityForResult(this, scanPassportIntent, REQUEST_CAPTURE_PASSPORT);
    }

    private void scanSelfie() throws JSONException {
        Intent scanSelfieIntent = new Intent(this.cordova.getActivity(), SelfieActivity.class);
        this.cordova.startActivityForResult(this, scanSelfieIntent, REQUEST_CAPTURE_SELFIE);
    }


    private void openSetting(CallbackContext callbackContext) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        String packageName = this.cordova.getActivity().getApplicationContext().getPackageName();
        Uri uri = Uri.parse("package:" + packageName);
        intent.setData(uri);
        this.cordova.getActivity().startActivity(intent);
        callbackContext.success("true");
    }
}
