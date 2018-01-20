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

package com.ngengs.android.app.filkomnewsreader.utils.glideapp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class CustomSimpleTarget<Z> extends SimpleTarget<Z> {
    private ResourceReady<Z> mListener;

    public CustomSimpleTarget(ResourceReady<Z> mListener) {
        super();
        this.mListener = mListener;
    }

    @SuppressWarnings("unused")
    public CustomSimpleTarget(int width, int height, ResourceReady<Z> mListener) {
        super(width, height);
        this.mListener = mListener;
    }

    @Override
    public void onResourceReady(Z resource, Transition<? super Z> transition) {
        mListener.onResourceReady(resource);
    }


    public interface ResourceReady<Z> {
        void onResourceReady(Z resource);
    }
}
