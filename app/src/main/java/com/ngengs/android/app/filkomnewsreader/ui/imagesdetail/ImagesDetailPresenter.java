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

import com.ngengs.android.app.filkomnewsreader.utils.CommonUtils;

public class ImagesDetailPresenter implements ImagesDetailContract.Presenter {

    private final ImagesDetailContract.View mView;
    private String mImageUrl;

    ImagesDetailPresenter(ImagesDetailContract.View mView) {
        if (mView != null) {
            this.mView = mView;
            this.mView.setPresenter(this);
        } else {
            throw new RuntimeException("Cant bind view");
        }
    }

    @Override
    public void start() {
        if (mImageUrl == null) {
            mView.stop();
        } else {
            mView.loadImage(mImageUrl);
        }
    }

    @Override
    public void setImage(String url) {
        mImageUrl = url;
    }

    @Override
    public void saveImage() {
        String imageName = CommonUtils.getFileNameFromUrl(mImageUrl);
        if (imageName != null && !imageName.equals("")) {
            mView.saveImage(imageName);
        }
    }
}
