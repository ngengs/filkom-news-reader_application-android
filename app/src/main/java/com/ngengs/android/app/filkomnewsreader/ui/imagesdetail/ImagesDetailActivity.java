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

package com.ngengs.android.app.filkomnewsreader.ui.imagesdetail;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.bumptech.glide.load.DecodeFormat;
import com.github.chrisbanes.photoview.PhotoView;
import com.ngengs.android.app.filkomnewsreader.R;
import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;
import com.ngengs.android.app.filkomnewsreader.utils.glideapp.CustomSimpleTarget;
import com.ngengs.android.app.filkomnewsreader.utils.glideapp.GlideApp;

import timber.log.Timber;

public class ImagesDetailActivity extends AppCompatActivity implements ImagesDetailContract.View {

    private ImagesDetailContract.Presenter mPresenter;
    public static final String INTENT_ARGS_DATA = "DATA";
    private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;
    private PhotoView mImageDetail;
    private FloatingActionButton mImageSaveButton;
    private boolean mHide;
    private Bitmap mImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_detail);
        mImageDetail = findViewById(R.id.image_detail);
        mImageSaveButton = findViewById(R.id.image_save);
        mImageSaveButton.setOnClickListener(view -> mPresenter.saveImage());
        mImageSaveButton.hide();
        mHide = false;
        mImageDetail.setOnScaleChangeListener(
                (scaleFactor, focusX, focusY) -> toggleUi(!(scaleFactor < 1 || scaleFactor > 1)));
        String data = null;
        if (getIntent() != null) {
            data = getIntent().getStringExtra(INTENT_ARGS_DATA);
        }
        mPresenter = new ImagesDetailPresenter(this);
        mPresenter.setImage(data);
        mPresenter.start();

    }

    @Override
    public void setPresenter(@NonNull ImagesDetailContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void stop() {
        finish();
    }

    @Override
    public void loadImage(@NonNull String url) {
        GlideApp.with(this)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .load(url)
                .thumbnail(0.05f)
                .into(new CustomSimpleTarget<>(resource -> {
                    mImageBitmap = resource;
                    mImageDetail.setImageBitmap(mImageBitmap);
                    mImageSaveButton.show();
                }));
    }

    @Override
    public void saveImage(@NonNull String imageName) {
        Timber.d("saveImage() called with: imageName = [ %s ]", imageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkPermission = ContextCompat.checkSelfPermission(this,
                                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                                                  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                  REQUEST_CODE_PERMISSION_STORAGE);
                return;
            }
        }
        String imagePath = CommonUtils.saveImage(this, mImageBitmap, imageName);
        Timber.d("saveImage: %s", imagePath);
        if (!TextUtils.isEmpty(imagePath)) {
            Snackbar.make(mImageSaveButton, "Berhasil menyimpan gambar", Snackbar.LENGTH_SHORT).show();
            CommonUtils.notifyGallery(this, imagePath);
        } else {
            Snackbar.make(mImageSaveButton, "Gagal menyimpan gambar", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void toggleUi(boolean hide) {
        if (this.mHide != hide && mImageBitmap != null) {
            this.mHide = hide;
            if (!this.mHide) {
                Timber.d("toggleUi: mHide: false");
                mImageSaveButton.show();
            } else {
                Timber.d("toggleUi: mHide: true");
                mImageSaveButton.hide();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PERMISSION_STORAGE){
            Timber.d("onActivityResult: %s", "Result from permission storage");
            mPresenter.saveImage();
        }
    }
}
