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

package com.learnNcode.mediachooser.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.learnNcode.mediachooser.MediaChooserConstants;
import com.learnNcode.mediachooser.R;
import com.learnNcode.mediachooser.fragment.ImageFragment;
import com.learnNcode.mediachooser.fragment.VideoFragment;

public class HomeFragmentActivity extends FragmentActivity implements ImageFragment.OnImageSelectedListener, 
VideoFragment.OnVideoSelectedListener{


	private FragmentTabHost mTabHost;
	private TextView headerBarTitle;
	private ImageView headerBarCamera;
	private ImageView headerBarBack;

	private Uri fileUri;
	private boolean mIsFromBucket = false;


	private final Handler handler = new Handler();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_home_media_chooser);

		headerBarTitle  = (TextView)findViewById(R.id.titleTextViewFromHeaderBar);
		headerBarCamera = (ImageView)findViewById(R.id.cameraImageViewFromHeaderBar);
		headerBarBack   = (ImageView)findViewById(R.id.backArrowImageViewFromHeaderView);
		mTabHost        = (FragmentTabHost) findViewById(android.R.id.tabhost);



		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabFrameLayoutFromMediaChooser);

		if(getIntent() != null && (getIntent().getBooleanExtra("isFromBucket", false))){

			mIsFromBucket = true;
			if(getIntent().getBooleanExtra("image", false)){
				headerBarTitle.setText(getResources().getString(R.string.image));
				headerBarCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_camera_button));
				headerBarCamera.setTag(getResources().getString(R.string.image));

				Bundle bundle = new Bundle();
				bundle.putString("name", getIntent().getStringExtra("name"));
				mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.image), null), ImageFragment.class, bundle);

			}else{
				headerBarTitle.setText(getResources().getString(R.string.video));
				headerBarCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_video_button));
				headerBarCamera.setTag(getResources().getString(R.string.video));

				Bundle bundle = new Bundle();
				bundle.putString("name", getIntent().getStringExtra("name"));
				mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.video), null), VideoFragment.class, bundle);
			}
		}else{

			headerBarTitle.setText(getResources().getString(R.string.video));
			headerBarCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_video_button));
			headerBarCamera.setTag(getResources().getString(R.string.video));

			mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(getString(R.string.video), null), VideoFragment.class, null);
			mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(getString(R.string.image), null), ImageFragment.class, null);

		}


		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
				ImageFragment imageFragment  = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
				VideoFragment videoFragment  = (VideoFragment) fragmentManager.findFragmentByTag("tab2");
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


				if(tabId.equalsIgnoreCase("tab1")){

					headerBarTitle.setText(getResources().getString(R.string.image));
					headerBarCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_camera_button));
					headerBarCamera.setTag(getResources().getString(R.string.image));

					if(imageFragment != null){   
						if(videoFragment != null){
							fragmentTransaction.hide(videoFragment);
						}
						fragmentTransaction.show(imageFragment); 
					}
				}else{ 
					headerBarTitle.setText(getResources().getString(R.string.video));
					headerBarCamera.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_video_button));
					headerBarCamera.setTag(getResources().getString(R.string.video));

					if(videoFragment != null){

						if(imageFragment != null){
							fragmentTransaction.hide(imageFragment);
						}

						fragmentTransaction.show(videoFragment);   
						videoFragment.getAdapter().notifyDataSetChanged();
					}
				}
				fragmentTransaction.commit();         
			}
		});

		headerBarBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		headerBarCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				if(view.getTag().toString().equals(getResources().getString(R.string.video))){
					Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);


					fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_VIDEO); // create a file to save the image

					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);

				}else{
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputMediaFileUri(MediaChooserConstants.MEDIA_TYPE_IMAGE); // create a file to save the image

					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}
			}
		});
	}

	/** Create a file Uri for saving an image or video */
	private Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MediaChooserConstants.folderName);
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MediaChooserConstants.MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if(type == MediaChooserConstants.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == MediaChooserConstants.CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK ) {

				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//Do something after 2000ms
						String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
						android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
						VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag("tab2");
						//						
						if(videoFragment == null){   
							VideoFragment newVideoFragment = new VideoFragment();
							newVideoFragment.addItem(fileUriString);

						}else{
							videoFragment.addItem(fileUriString);
						}
					}
				}, 2000);

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the video capture
			} else {
				// Video capture failed, advise user
			}
		}else if (requestCode == MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK ) {
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//Do something after 2000ms
						String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
						android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
						ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
						if(imageFragment == null){   
							ImageFragment newImageFragment = new ImageFragment();
							newImageFragment.addItem(fileUriString);

						}else{
							imageFragment.addItem(fileUriString);
						}
					}
				}, 2000);
			} 
		}
	}

	@Override
	public void onImageSelected(int count) {
		/*if(count != 0){
			((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText("Images     " + count);

		}else{
			((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText("Images");
		}*/

		if( mTabHost.getTabWidget().getChildAt(1) != null){
			if(count != 0){
				((TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getString(R.string.images_tab) + "    " + count);

			}else{
				((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getString(R.string.image));
			}
		}else {
			if(count != 0){
				((TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getString(R.string.images_tab) + "    "  + count);

			}else{
				((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getString(R.string.image));
			}
		}
	}


	@Override
	public void onVideoSelected(int count) {

		if(count != 0){
			((TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getString(R.string.videos_tab) + "    "  + count);

		}else{
			((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getString(R.string.video));
		}
	}

	@Override
	public void finish() {
		if(! mIsFromBucket){
			android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
			ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag("tab1");
			VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag("tab2");

			if(videoFragment != null){
				Intent videoIntent = new Intent();
				videoIntent.setAction(MediaChooserConstants.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
				videoIntent.putStringArrayListExtra("list", videoFragment.getSelectedVideoList());
				sendBroadcast(videoIntent);
			}

			if(imageFragment != null){
				Intent imageIntent = new Intent();
				imageIntent.setAction(MediaChooserConstants.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
				imageIntent.putStringArrayListExtra("list", imageFragment.getSelectedImageList());
				sendBroadcast(imageIntent);
			}
		}
		super.finish();
	}
}
