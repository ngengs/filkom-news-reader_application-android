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

package com.ngengs.android.app.filkomnewsreader.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ngengs.android.app.filkomnewsreader.R;

public final class CommonUtils {
    public static void openLinkInBrowser(Context context, String uriString) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    public static void shareLink(Context context, String title, String uriString) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, title + " " + uriString);
        sendIntent.setType("text/plain");
        context.startActivity(
                Intent.createChooser(sendIntent, context.getString(R.string.send_to)));
    }
}
