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

package com.learnncode.mediachooser.sample;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.activity.BucketHomeFragmentActivity;
import com.learnncode.mediachooser.activity.HomeFragmentActivity;

public class MainActivity extends Activity {

	Button folderViewButton;
	Button fileViewButton;
	GridView gridView;
	MediaGridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		folderViewButton = (Button)findViewById(R.id.folderButton);
		fileViewButton = (Button)findViewById(R.id.fileButton);
		gridView = (GridView)findViewById(R.id.gridView);
		folderViewButton.setOnClickListener(clickListener);
		fileViewButton.setOnClickListener(clickListener);

		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(imageBroadcastReceiver, imageIntentFilter);

	}


	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(view == folderViewButton){
				MediaChooser.setSelectionLimit(20);
				Intent intent = new Intent(MainActivity.this, BucketHomeFragmentActivity.class);
				startActivity(intent);

			}else {
				Intent intent = new Intent(MainActivity.this, HomeFragmentActivity.class);
				startActivity(intent);
			}
		}
	};

	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			Toast.makeText(MainActivity.this, "yippiee Video ", Toast.LENGTH_SHORT).show();
			Toast.makeText(MainActivity.this, "Video SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			setAdapter(intent.getStringArrayListExtra("list"));
		}
	};


	BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(MainActivity.this, "yippiee Image ", Toast.LENGTH_SHORT).show();
			Toast.makeText(MainActivity.this, "Image SIZE :" + intent.getStringArrayListExtra("list").size(), Toast.LENGTH_SHORT).show();
			setAdapter(intent.getStringArrayListExtra("list"));
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(imageBroadcastReceiver);
		unregisterReceiver(videoBroadcastReceiver);
		super.onDestroy();
	}

	private void setAdapter( List<String> filePathList) {
		if(adapter == null){
			adapter = new MediaGridViewAdapter(MainActivity.this, 0, filePathList);
			gridView.setAdapter(adapter);
		}else{
			adapter.addAll(filePathList);
			adapter.notifyDataSetChanged();
		}
	}
}
