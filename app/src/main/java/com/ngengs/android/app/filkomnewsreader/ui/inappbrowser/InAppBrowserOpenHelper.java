/*******************************************************************************
 * Copyright (c) 2017 Rizky Kharisma (@ngengs)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ngengs.android.app.filkomnewsreader.ui.inappbrowser;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public final class InAppBrowserOpenHelper {
    private static final String STABLE_PACKAGE = "com.android.chrome";
    private static final String BETA_PACKAGE = "com.chrome.beta";
    private static final String DEV_PACKAGE = "com.chrome.dev";
    private static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
    private static final String ACTION_CUSTOM_TABS_CONNECTION
            = "android.support.customtabs.action.CustomTabsService";

    public static void open(@NonNull Context context, @NonNull String url) {
        String packageName = getPackageNameToUse(context);

        //If we cant find a package name, it means theres no browser that supports
        //Chrome Custom Tabs installed. So, we fallback to the webview
        if (packageName == null) {
            Intent intent = new Intent(context, InAppBrowserActivity.class);
            intent.putExtra(InAppBrowserActivity.TAG_URL, url);
            context.startActivity(intent);
        } else {
            PendingIntent intent = PendingIntent.getActivity(context, 0,
                                                             CommonUtils.shareLink(url),
                                                             PendingIntent.FLAG_UPDATE_CURRENT);
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .setShowTitle(true)
                    .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .addMenuItem(context.getString(R.string.send), intent)
                    .build();
            customTabsIntent.intent.setPackage(packageName);
            customTabsIntent.launchUrl(context, Uri.parse(url));
        }
    }

    private static String getPackageNameToUse(@NonNull Context context) {
        PackageManager pm = context.getPackageManager();// Get default VIEW intent handler.
        Intent activityIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://filkom.ub.ac.id"));
        ResolveInfo defaultViewHandlerInfo = pm.resolveActivity(activityIntent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // Get all apps that can handle VIEW intents.
        List<ResolveInfo> resolvedActivityList = pm.queryIntentActivities(activityIntent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo info : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(info.activityInfo.packageName);
            if (pm.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(info.activityInfo.packageName);
            }
        }

        // Now packagesSupportingCustomTabs contains all apps that can handle both VIEW intents
        // and service calls.
        if (packagesSupportingCustomTabs.isEmpty()) {
            return null;
        } else if (packagesSupportingCustomTabs.size() == 1) {
            return packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName)
                   && !hasSpecializedHandlerIntents(context, activityIntent)
                   && packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            return defaultViewHandlerPackageName;
        } else if (packagesSupportingCustomTabs.contains(STABLE_PACKAGE)) {
            return STABLE_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(BETA_PACKAGE)) {
            return BETA_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(DEV_PACKAGE)) {
            return DEV_PACKAGE;
        } else if (packagesSupportingCustomTabs.contains(LOCAL_PACKAGE)) {
            return LOCAL_PACKAGE;
        }
        return null;
    }


    private static boolean hasSpecializedHandlerIntents(@NonNull Context context, Intent intent) {
        try {
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> handlers = pm.queryIntentActivities(
                    intent,
                    PackageManager.GET_RESOLVED_FILTER);
            if (handlers == null || handlers.size() == 0) {
                return false;
            }
            for (ResolveInfo resolveInfo : handlers) {
                IntentFilter filter = resolveInfo.filter;
                if (filter == null) {
                    continue;
                }
                if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                    continue;
                }
                if (resolveInfo.activityInfo == null) {
                    continue;
                }
                return true;
            }
        } catch (RuntimeException e) {
            Timber.e(e, "hasSpecializedHandlerIntents: ");
        }
        return false;
    }
}
