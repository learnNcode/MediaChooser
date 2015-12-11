package com.learnncode.mediachooser.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.learnncode.mediachooser.R;
import com.learnncode.mediachooser.Utilities.BucketEntry;
import com.learnncode.mediachooser.Utilities.MediaChooserConstants;
import com.learnncode.mediachooser.activity.HomeScreenMediaChooser;
import com.learnncode.mediachooser.adapter.BucketGridAdapter;

import java.util.ArrayList;


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

public class BucketImageFragment extends Fragment {

    // The indices should match the following projections.
    private final int INDEX_BUCKET_ID = 0;
    private final int INDEX_BUCKET_NAME = 1;
    private final int INDEX_BUCKET_URL = 2;

    private final String[] PROJECTION_BUCKET = {
            ImageColumns.BUCKET_ID,
            ImageColumns.BUCKET_DISPLAY_NAME,
            ImageColumns.DATA};

    private View mView;
    private GridView mGridView;
    private BucketGridAdapter mBucketAdapter;

    private Cursor mCursor;


    public BucketImageFragment() {
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().getWindow().setBackgroundDrawable(null);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter(MediaChooserConstants.BRAODCAST_IMAGE_RECEIVER));

        if (mView == null) {
            mView = inflater.inflate(R.layout.view_grid_layout_media_chooser, container, false);
            mGridView = (GridView) mView.findViewById(R.id.gridViewFromMediaChooser);
            init();
        } else {
            if (mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
            if (mBucketAdapter == null) {
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
            }
        }
        return mView;
    }


    private void init() {
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        mCursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, PROJECTION_BUCKET, null, null, orderBy + " DESC");
        ArrayList<BucketEntry> buffer = new ArrayList<BucketEntry>();
        try {
            while (mCursor.moveToNext()) {
                BucketEntry entry = new BucketEntry(
                        mCursor.getInt(INDEX_BUCKET_ID),
                        mCursor.getString(INDEX_BUCKET_NAME), mCursor.getString(INDEX_BUCKET_URL));

                if (!buffer.contains(entry)) {
                    buffer.add(entry);
                }
            }

            if (mCursor.getCount() > 0) {
                mBucketAdapter = new BucketGridAdapter(getActivity(), buffer, false);
                mGridView.setAdapter(mBucketAdapter);
            } else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.no_media_file_available), Toast.LENGTH_SHORT).show();
            }

            mGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapter, View view,
                                        int position, long id) {

                    BucketEntry bucketEntry = (BucketEntry) adapter.getItemAtPosition(position);
                    Intent selectImageIntent = new Intent(getActivity(), HomeScreenMediaChooser.class);
                    selectImageIntent.putExtra("name", bucketEntry.bucketName);
                    selectImageIntent.putExtra("image", true);
                    selectImageIntent.putExtra("isFromBucket", true);
                    getActivity().startActivityForResult(selectImageIntent, MediaChooserConstants.BUCKET_SELECT_IMAGE_CODE);
                }
            });

        } finally {
            mCursor.close();
        }
    }

    public BucketGridAdapter getAdapter() {
        return mBucketAdapter;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mBucketAdapter.addLatestEntry(intent.getStringExtra("value"));
            mBucketAdapter.notifyDataSetChanged();
        }
    };


    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
