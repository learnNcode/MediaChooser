/*
 * Copyright 2013 - learnNcode (learnncode@gmail.com)
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


package com.learnncode.mediachooser.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.learnncode.mediachooser.BucketEntry;
import com.learnncode.mediachooser.MediaChooserConstants;
import com.learnncode.mediachooser.R;
import com.learnncode.mediachooser.async.ImageLoadAsync;
import com.learnncode.mediachooser.async.MediaAsync;
import com.learnncode.mediachooser.async.VideoLoadAsync;
import com.learnncode.mediachooser.fragment.BucketVideoFragment;

public class BucketGridAdapter extends ArrayAdapter<BucketEntry> {

	public BucketVideoFragment bucketVideoFragment; 

	private Context mContext;
	private ArrayList<BucketEntry> mBucketEntryList;
	private boolean mIsFromVideo;
	private int mWidth;

	public BucketGridAdapter(Context context, int resource, ArrayList<BucketEntry> categories, boolean isFromVideo) {
		super(context, resource, categories);
		mBucketEntryList = categories;
		mContext         = context;
		mIsFromVideo     = isFromVideo;
	}

	public int getCount() {
		return mBucketEntryList.size();
	}

	@Override
	public BucketEntry getItem(int position) {
		return mBucketEntryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addLatestEntry(String url) {
		int count = mBucketEntryList.size();
		boolean success = false;
		for(int i = 0; i< count; i++){
			if(mBucketEntryList.get(i).bucketName.equals(MediaChooserConstants.folderName)){
				mBucketEntryList.get(i).bucketUrl = url;
				success = true;
				break;
			}
		}

		if(!success){
			BucketEntry latestBucketEntry = new BucketEntry(0, MediaChooserConstants.folderName, url);
			mBucketEntryList.add(0, latestBucketEntry);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {

			Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay(); 
			mWidth = display.getWidth();  // deprecated

			LayoutInflater viewInflater;
			viewInflater = LayoutInflater.from(mContext);
			convertView  = viewInflater.inflate(R.layout.view_grid_bucket_item_media_chooser, parent, false);

			holder = new ViewHolder();
			holder.imageView    = (ImageView) convertView.findViewById(R.id.imageViewFromMediaChooserBucketRowView);
			holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextViewFromMediaChooserBucketRowView);

			convertView.setTag(holder);

		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		FrameLayout.LayoutParams imageParams = (FrameLayout.LayoutParams) holder.imageView.getLayoutParams();
		imageParams.width  = mWidth/2;
		imageParams.height = mWidth/2;

		holder.imageView.setLayoutParams(imageParams);

		if(mIsFromVideo){
			new VideoLoadAsync(bucketVideoFragment, holder.imageView, false, mWidth/2).executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mBucketEntryList.get(position).bucketUrl.toString());

		}else{
			ImageLoadAsync loadAsync = new ImageLoadAsync(mContext, holder.imageView, mWidth/2);
			loadAsync.executeOnExecutor(MediaAsync.THREAD_POOL_EXECUTOR, mBucketEntryList.get(position).bucketUrl);
		}

		holder.nameTextView.setText(mBucketEntryList.get(position).bucketName );
		return convertView;
	}

	class ViewHolder {
		ImageView imageView;
		TextView nameTextView;
	}
}


