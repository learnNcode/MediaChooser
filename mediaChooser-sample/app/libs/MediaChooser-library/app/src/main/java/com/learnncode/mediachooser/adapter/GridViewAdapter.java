package com.learnncode.mediachooser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

import com.learnncode.mediachooser.R;
import com.learnncode.mediachooser.Utilities.MediaModel;
import com.learnncode.mediachooser.async.ImageLoadAsync;
import com.learnncode.mediachooser.async.MediaAsync;
import com.learnncode.mediachooser.async.VideoLoadAsync;
import com.learnncode.mediachooser.fragment.VideoFragment;

import java.util.List;


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

public class GridViewAdapter extends ArrayAdapter<MediaModel> {
    public VideoFragment videoFragment;

    private Context mContext;
    private List<MediaModel> mGalleryModelList;
    private int mWidth;
    private boolean mIsFromVideo;
    private LayoutInflater mViewInflater;


    public GridViewAdapter(Context context, List<MediaModel> categories, boolean isFromVideo) {
        super(context, 0, categories);
        mGalleryModelList = categories;
        mContext = context;
        mIsFromVideo = isFromVideo;
        mViewInflater = LayoutInflater.from(mContext);
    }

    public void addLatestEntry(MediaModel mediaModel) {
        if (mediaModel != null) {
            mGalleryModelList.add(0, mediaModel);
        }
        notifyDataSetChanged();
    }

    public int getCount() {
		return mGalleryModelList.size();
    }

    @Override
    public MediaModel getItem(int position) {
        return mGalleryModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            mWidth = mContext.getResources().getDisplayMetrics().widthPixels;

            convertView = mViewInflater.inflate(R.layout.view_grid_item_media_chooser, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromMediaChooserGridItemRowView);
            holder.checkedView = (FrameLayout) convertView.findViewById(R.id.checkedViewFromMediaChooserGridItemRowView);

            LayoutParams imageParams = (LayoutParams) holder.imageView.getLayoutParams();
            imageParams.width = mWidth / 2;
            imageParams.height = mWidth / 2;

            holder.imageView.setLayoutParams(imageParams);

            holder.checkedView.setLayoutParams(imageParams);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // set the status according to this Category item

        if (mIsFromVideo) {
            if (!mGalleryModelList.get(position).isCallStarted) {

                mGalleryModelList.get(position).isCallStarted = true;
                new VideoLoadAsync(videoFragment, holder.imageView, false, mWidth / 2).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url);
            }
        } else {
            if (!mGalleryModelList.get(position).isCallStarted) {
                mGalleryModelList.get(position).isCallStarted = true;
                ImageLoadAsync loadAsync = new ImageLoadAsync(mContext, holder.imageView, mWidth / 2);
                loadAsync.executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mGalleryModelList.get(position).url);
            }
        }

        holder.checkedView.bringToFront();
        if (mGalleryModelList.get(position).status) {
            holder.checkedView.setVisibility(View.VISIBLE);
        } else {
            holder.checkedView.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        FrameLayout checkedView;
    }

}
