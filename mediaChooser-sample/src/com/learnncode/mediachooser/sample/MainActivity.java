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

import com.learnNcode.mediachooser.MediaChooserConstants;
import com.learnNcode.mediachooser.activity.BucketHomeFragmentActivity;
import com.learnNcode.mediachooser.activity.HomeFragmentActivity;

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

		IntentFilter videoIntentFilter = new IntentFilter(MediaChooserConstants.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		IntentFilter imageIntentFilter = new IntentFilter(MediaChooserConstants.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(imageBroadcastReceiver, imageIntentFilter);

	}



	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(view == folderViewButton){

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
