package com.learnncode.mediachooser.async;

import android.content.Context;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

import java.io.File;


/*
 * Copyright 2015 - learnNcode (learnncode@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public class ImageLoadAsync extends MediaAsync<String, String, String> {

    private ImageView mImageView;
    private Context mContext;
    private int mWidth;

    public ImageLoadAsync(Context context, ImageView imageView, int width) {
        mImageView = imageView;
        mContext = context;
        mWidth = width;
    }

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        return url;
    }

    @Override
    protected void onPostExecute(String result) {

        AQuery aQuery = new AQuery(mContext);
        aQuery.id(mImageView);
        aQuery.image(new File(result),true, mWidth, new BitmapAjaxCallback());
    }

}
