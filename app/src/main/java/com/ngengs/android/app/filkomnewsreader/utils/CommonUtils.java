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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ngengs.android.app.filkomnewsreader.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class CommonUtils {
    public static void openLinkInBrowser(@NonNull Context context, @NonNull String uriString) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    public static void shareLink(@NonNull Context context, @NonNull String title,
                                 @NonNull String uriString) {
        Intent sendIntent = CommonUtils.shareLink(title + " " + uriString);
        context.startActivity(
                Intent.createChooser(sendIntent, context.getString(R.string.send_to)));
    }

    public static Intent shareLink(@NonNull String textToShare) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    @Nullable
    public static String saveImage(@NonNull Context context, @NonNull Bitmap imageBitmap,
                                   @NonNull String imageName) {
        String savedImagePath = null;
        String appDirectoryName = context.getString(R.string.app_name_directory);
        File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), appDirectoryName);
        boolean canCreateDirectory = true;
        if (!imageRoot.exists()) {
            canCreateDirectory = imageRoot.mkdir();
        }
        if (canCreateDirectory) {
            File imageFile = new File(imageRoot, imageName);
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            }
            savedImagePath = imageFile.getAbsolutePath();
            try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
            } catch (FileNotFoundException e) {
                savedImagePath = null;
                e.printStackTrace();
            } catch (IOException e) {
                savedImagePath = null;
                e.printStackTrace();
            }
        }

        return savedImagePath;
    }

    public static void notifyGallery(@NonNull Context context, @NonNull String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imagePath);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    @Nullable
    public static String getFileNameFromUrl(@NonNull String urlString) {
        String fileName = null;
        try {
            URL url = new URL(urlString);
            urlString = url.getPath();
            fileName = urlString.substring(urlString.lastIndexOf("/") + 1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return fileName;
    }
}
