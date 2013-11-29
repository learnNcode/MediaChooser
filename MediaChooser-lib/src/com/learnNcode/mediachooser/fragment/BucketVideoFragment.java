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



package com.learnNcode.mediachooser.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.VideoColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.learnNcode.mediachooser.BucketEntry;
import com.learnNcode.mediachooser.MediaChooserConstants;
import com.learnNcode.mediachooser.R;
import com.learnNcode.mediachooser.activity.HomeFragmentActivity;
import com.learnNcode.mediachooser.adapter.BucketGridAdapter;

public class BucketVideoFragment extends Fragment{

	private final String TAG = "BucketVideoFragment";

	// The indices should match the following projections.
	private final int INDEX_BUCKET_ID 		= 0;
	private final int INDEX_BUCKET_NAME 	= 1;
	private final int INDEX_BUCKET_URL 		= 2;

	private static final String[] PROJECTION_BUCKET = {
		VideoColumns.BUCKET_ID,
		VideoColumns.BUCKET_DISPLAY_NAME,
		VideoColumns.DATA
	};

	private View mView;
	private BucketGridAdapter mBucketAdapter;
	private GridView mGridView;


	public BucketVideoFragment(){
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mView == null){
			mView     = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);
			mGridView = (GridView)mView.findViewById(R.id.gridViewFromMediaChooser);
			init();
		}else{
			((ViewGroup) mView.getParent()).removeView(mView);
		}

		return mView;
	}



	private void init(){
		final String orderBy = MediaStore.Video.Media.DATE_TAKEN;
		Cursor cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET, null, null, orderBy + " DESC");
		ArrayList<BucketEntry> buffer = new ArrayList<BucketEntry>();
		try {
			while (cursor.moveToNext()) {
				BucketEntry entry = new BucketEntry(
						cursor.getInt(INDEX_BUCKET_ID),
						cursor.getString(INDEX_BUCKET_NAME),cursor.getString(INDEX_BUCKET_URL));

				if (! buffer.contains(entry)) {
					buffer.add(entry);
				}
			}
			mBucketAdapter = new BucketGridAdapter(getActivity(), 0, buffer, true);
			mBucketAdapter.bucketVideoFragment = this;
			mGridView.setAdapter(mBucketAdapter);

			mGridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int position, long id) {

					BucketEntry bucketEntry = (BucketEntry)adapter.getItemAtPosition(position);
					Intent selectImageIntent = new Intent(getActivity(),HomeFragmentActivity.class);
					selectImageIntent.putExtra("name", bucketEntry.bucketName);
					selectImageIntent.putExtra("isFromBucket", true);
					getActivity().startActivityForResult(selectImageIntent, MediaChooserConstants.BUCKET_SELECT_VIDEO_CODE);

				}
			});

		} finally {
			cursor.close();
		}
	}

	public BucketGridAdapter getAdapter() {
		if (mBucketAdapter != null) {
			return mBucketAdapter;
		}
		return null;
	}

}
