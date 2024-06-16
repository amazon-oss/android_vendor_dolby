/******************************************************************************
 *  This program is protected under international and U.S. copyright laws as
 *  an unpublished work. This program is confidential and proprietary to the
 *  copyright owners. Reproduction or disclosure, in whole or in part, or the
 *  production of derivative works therefrom without the express permission of
 *  the copyright owners is prohibited.
 *
 *                 Copyright (C) 2013 - 2014 by Dolby Laboratories,
 *                             All rights reserved.
 ******************************************************************************/

/*
 * DsConfiguration.java
 *
 */
package com.dolby.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.dolby.ds.DsManager;
import com.dolby.api.DsLog;

public class DsConfiguration {
    private static final String TAG = "DsConfiguration";
    public static final boolean isDefaultSettingsOnFileSystem = true;
    private static final String DS_DEFAULT_SETTINGS_USER_PATH = "/data/dolby";
    private static final String DS_DEFAULT_SETTINGS_VENDOR_PATH = "/system/vendor/etc/dolby";
    private static final String DS_DEFAULT_SETTINGS_FILENAME = "ds-default.xml";

    public static InputStream prepare(Context context, String dirPath) {
        String userSettingsPath = null;
        InputStream defaultInStream = null;

        try {
            File file = new File(dirPath, DsManager.DS_CURRENT_FILENAME);
            if (file.exists()) {
                DsLog.log1(TAG, file.getAbsolutePath() + " already exists");
            } else {
                DsLog.log1(TAG, "Creating " + file.getAbsolutePath());
                FileOutputStream fos = context.openFileOutput(DsManager.DS_CURRENT_FILENAME, Context.MODE_PRIVATE);
                fos.close();
            }

            file = new File(dirPath, DsManager.DS_STATE_FILENAME);
            if (file.exists()) {
                DsLog.log1(TAG, file.getAbsolutePath() + " already exists");
            } else {
                DsLog.log1(TAG, "Creating " + file.getAbsolutePath());
                FileOutputStream fos = context.openFileOutput(DsManager.DS_STATE_FILENAME, Context.MODE_PRIVATE);
                fos.close();
            }

            // Read the property value
            String dsFileName = android.os.SystemProperties.get("dolby.ds.settings.filename", DS_DEFAULT_SETTINGS_FILENAME);

            if (isDefaultSettingsOnFileSystem) {
                file = new File(DS_DEFAULT_SETTINGS_VENDOR_PATH, dsFileName);
                if (file.exists()) {
                    userSettingsPath = file.getAbsolutePath();
                } else {
                    userSettingsPath = DS_DEFAULT_SETTINGS_USER_PATH + "/" + dsFileName;
                }
                DsLog.log1(TAG, "Adopting the file system settings... " + userSettingsPath);
                if (userSettingsPath != null) {
                    defaultInStream = new FileInputStream(userSettingsPath);
                } else {
                    Log.e(TAG, "The user settings path NOT defined!");
                }
            } else {
                DsLog.log1(TAG, "Adopting the built-in settings in assets...");
                AssetManager am = context.getAssets();
                defaultInStream = am.open(dsFileName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception was caught");
            e.printStackTrace();
            defaultInStream = null;
        }
        return defaultInStream;
    }
}
