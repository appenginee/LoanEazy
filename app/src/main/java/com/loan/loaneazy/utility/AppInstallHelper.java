package com.loan.loaneazy.utility;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.loan.loaneazy.model.epoc.InstallApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppInstallHelper {
    private static final String TAG = "AppInstallHelper";

    public static ArrayList<InstallApp> getInstallApp(Context mCtx) {
        ArrayList<InstallApp> mInstallList = new ArrayList<>();
        PackageManager pm = mCtx.getPackageManager();
        Intent main = new Intent(Intent.ACTION_MAIN, null);
        main.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> infos = pm.queryIntentActivities(main, 0);
        Collections.sort(infos, new ResolveInfo.DisplayNameComparator(pm));
        if (null != infos && !infos.isEmpty()) {
            for (ResolveInfo inf : infos) {
                if (null != inf) {
                    try {
                        String mAppName = getAppName(inf.activityInfo.packageName, pm);
                        String mPackageName = inf.activityInfo.packageName;
                        PackageInfo pInfo = mCtx.getPackageManager().getPackageInfo(mPackageName, 0);
                        String version = pInfo.versionName;
                        int versionCode = pInfo.versionCode;
                        long firstInstallTime = pInfo.firstInstallTime;
                        long lastUpdateTime = pInfo.lastUpdateTime;
                        InstallApp mInstallApp = new InstallApp(
                                mAppName,
                                mPackageName,
                                version,
                                String.valueOf(firstInstallTime),
                                String.valueOf(lastUpdateTime)
                        );
                        mInstallList.add(mInstallApp);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
        if (mInstallList.size() > 0) {
            String param = new Gson().toJson(mInstallList);
            //Log.e(TAG, "getInstallApp: " + param);
            return mInstallList;
        } else {
            return null;
        }

    }

    public static String getAppName(String pkg, PackageManager pm) {
        String nm = pkg;
        try {
            CharSequence cs = pm.getApplicationLabel(pm.getApplicationInfo(pkg, PackageManager.GET_META_DATA));
            nm = cs.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ;
        return nm;
    }


}
