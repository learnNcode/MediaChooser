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

package com.learnncode.mediachooser.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.learnncode.mediachooser.MediaChooser;
import com.learnncode.mediachooser.MediaChooserConstants;
import com.learnncode.mediachooser.R;
import com.learnncode.mediachooser.fragment.ImageFragment;
import com.learnncode.mediachooser.fragment.VideoFragment;


public class HomeFragmentActivity extends FragmentActivity implements ImageFragment.OnImageSelectedListener,
        VideoFragment.OnVideoSelectedListener{


    public static final String TAB_IMAGE = "tab1";
    public static final String TAB_VIDEO = "tab2";
    protected FragmentTabHost mTabHost;
    private TextView headerBarTitle;
    private ImageView headerBarCamera;
    private ImageView headerBarBack;
    private TextView headerBarDone;

    private static Uri fileUri;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setupContentView();

        setupHeaderUI();

        setupTabHost();

        setupTabHostChangedListener();

    }

    protected void setupContentView() {
        setContentView(R.layout.activity_home_media_chooser);
    }

    protected void setupHeaderUI() {
        headerBarBack = (ImageView) findViewById(R.id.backArrowImageViewFromMediaChooserHeaderView);
        headerBarBack.setOnClickListener(clickListener);

        headerBarTitle = (TextView) findViewById(R.id.titleTextViewFromMediaChooserHeaderBar);

        headerBarCamera = (ImageView)findViewById(R.id.cameraImageViewFromMediaChooserHeaderBar);
        headerBarCamera.setOnClickListener(clickListener);
        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) headerBarCamera.getLayoutParams();
        params.height = convertDipToPixels(40);
        params.width  = convertDipToPixels(40);
        headerBarCamera.setLayoutParams(params);
        headerBarCamera.setScaleType(ScaleType.CENTER_INSIDE);
        headerBarCamera.setPadding(convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15), convertDipToPixels(15));
        if(! MediaChooserConstants.showCameraVideo){
            headerBarCamera.setVisibility(View.GONE);
        }

        headerBarDone   = (TextView)findViewById(R.id.doneTextViewViewFromMediaChooserHeaderView);
        headerBarDone.setOnClickListener(clickListener);
    }

    protected void setHeaderTitle(int id_text, int id_image) {
        headerBarTitle.setText(getResources().getString(R.string.image));
        setHeaderBarCameraBackground(getResources().getDrawable(R.drawable.selector_camera_button));
        headerBarCamera.setTag(getResources().getString(R.string.image));
    }

    protected void setupTabHost() {
        mTabHost        = (FragmentTabHost) findViewById(android.R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), R.id.realTabcontent);

        if(getIntent() == null)
            setIntent(new Intent());

        if(getIntent().getBooleanExtra("isFromBucket", false)){

            if(getIntent().getBooleanExtra("image", false)){
                setHeaderTitle(R.string.image, R.drawable.selector_camera_button);

                Bundle bundle = new Bundle();
                bundle.putString("name", getIntent().getStringExtra("name"));
                mTabHost.addTab(mTabHost.newTabSpec(TAB_IMAGE).setIndicator(getResources().getString(R.string.images_tab) + "     "), getImageFragmentClass(), bundle);

            }else{
                setHeaderTitle(R.string.video, R.drawable.selector_video_button);

                Bundle bundle = new Bundle();
                bundle.putString("name", getIntent().getStringExtra("name"));
                mTabHost.addTab(mTabHost.newTabSpec(TAB_VIDEO).setIndicator(getResources().getString(R.string.videos_tab) + "      "), getVideoFragmentClass(), bundle);
            }
        }else{

            if(getIntent().getBooleanExtra("showVideo", MediaChooserConstants.showVideo)){
                setHeaderTitle(R.string.video, R.drawable.selector_video_button);

                mTabHost.addTab(mTabHost.newTabSpec(TAB_VIDEO).setIndicator(getResources().getString(R.string.videos_tab) + "      "), getVideoFragmentClass(), null);
            }

            if(getIntent().getBooleanExtra("showImage", MediaChooserConstants.showImage)){
                setHeaderTitle(R.string.image, R.drawable.selector_camera_button);

                mTabHost.addTab(mTabHost.newTabSpec(TAB_IMAGE).setIndicator(getResources().getString(R.string.images_tab) + "      "), getImageFragmentClass(), null);
            }
        }

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {

            setupTabTitle(i);

        }

        if((mTabHost.getTabWidget().getChildAt(0) != null)){
            changeTabTitleUnselected(0);
        }

        if((mTabHost.getTabWidget().getChildAt(1) != null)){
            changeTabTitleSelected(1);
        }
    }

    protected void setupTabTitle(int i) {
        TextView textView = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
        if(textView.getLayoutParams() instanceof RelativeLayout.LayoutParams){

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            params.width  = RelativeLayout.LayoutParams.WRAP_CONTENT;
            textView.setGravity(Gravity.CENTER);
            textView.setLayoutParams(params);

        }else if(textView.getLayoutParams() instanceof LinearLayout.LayoutParams){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textView.getLayoutParams();
            params.gravity = Gravity.CENTER;
            textView.setLayoutParams(params);
        }
        textView.setTextColor(getResources().getColor(R.color.tabs_title_color));
        textView.setTextSize(convertDipToPixels(10));
    }

    protected Class<? extends VideoFragment> getVideoFragmentClass() {
        return VideoFragment.class;
    }

    protected Class<? extends ImageFragment> getImageFragmentClass() {
        return ImageFragment.class;
    }

    protected void setupTabHostChangedListener() {
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ImageFragment imageFragment  = (ImageFragment) fragmentManager.findFragmentByTag(TAB_IMAGE);
                VideoFragment videoFragment  = (VideoFragment) fragmentManager.findFragmentByTag(TAB_VIDEO);
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                if(tabId.equalsIgnoreCase(TAB_IMAGE)){
                    setHeaderTitle(R.string.image, R.drawable.selector_camera_button);

                    if(imageFragment != null){

                        if(videoFragment != null){
                            fragmentTransaction.hide(videoFragment);
                        }
                        fragmentTransaction.show(imageFragment);
                    }
                    changeTabTitleSelected(0);
                    changeTabTitleUnselected(1);

                }else{
                    setHeaderTitle(R.string.video, R.drawable.selector_video_button);

                    if(videoFragment != null){

                        if(imageFragment != null){
                            fragmentTransaction.hide(imageFragment);
                        }

                        fragmentTransaction.show(videoFragment);
                        if(videoFragment.getAdapter() != null){
                            videoFragment.getAdapter().notifyDataSetChanged();
                        }
                    }
                    changeTabTitleSelected(1);
                    changeTabTitleUnselected(0);
                }

                fragmentTransaction.commit();
            }
        });
    }

    protected void changeTabTitleUnselected(int tabNumber) {
        ((TextView)(mTabHost.getTabWidget().getChildAt(tabNumber).findViewById(android.R.id.title))).setTextColor(Color.WHITE);
    }

    protected void changeTabTitleSelected(int tabNumber) {
        ((TextView)(mTabHost.getTabWidget().getChildAt(tabNumber).findViewById(android.R.id.title))).setTextColor(getResources().getColor(R.color.headerbar_selected_tab_color));
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View view) {
            if(view == headerBarCamera){

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
            }else if(view == headerBarDone){

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag(TAB_IMAGE);
                VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(TAB_VIDEO);

                if(videoFragment != null || imageFragment != null){

                    if(videoFragment != null){
                        if(videoFragment.getSelectedVideoList() != null && videoFragment.getSelectedVideoList() .size() > 0){
                            Intent videoIntent = new Intent();
                            videoIntent.setAction(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER );
                            videoIntent.putStringArrayListExtra("list", videoFragment.getSelectedVideoList());
                            sendBroadcast(videoIntent);
                        }
                    }

                    if(imageFragment != null){
                        if(imageFragment.getSelectedImageList() != null && imageFragment.getSelectedImageList().size() > 0){
                            Intent imageIntent = new Intent();
                            imageIntent.setAction(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
                            imageIntent.putStringArrayListExtra("list", imageFragment.getSelectedImageList());
                            sendBroadcast(imageIntent);
                        }
                    }

                    finish();
                }else{
                    Toast.makeText(HomeFragmentActivity.this, getString(R.string.plaese_select_file), Toast.LENGTH_SHORT).show();
                }

            }else if(view == headerBarBack){
                finish();
            }
        }
    };

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
                final AlertDialog alertDialog = MediaChooserConstants.getDialog(HomeFragmentActivity.this).create();
                alertDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 5000ms
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        VideoFragment videoFragment = (VideoFragment) fragmentManager.findFragmentByTag(TAB_VIDEO);
                        //
                        if(videoFragment == null){
                            VideoFragment newVideoFragment = new VideoFragment();
                            newVideoFragment.addItem(fileUriString);

                        }else{
                            videoFragment.addItem(fileUriString);
                        }
                        alertDialog.cancel();
                    }
                }, 5000);


            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }else if (requestCode == MediaChooserConstants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK ) {

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));

                final AlertDialog alertDialog = MediaChooserConstants.getDialog(HomeFragmentActivity.this).create();
                alertDialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 5000ms
                        String fileUriString = fileUri.toString().replaceFirst("file:///", "/").trim();
                        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                        ImageFragment imageFragment = (ImageFragment) fragmentManager.findFragmentByTag(TAB_IMAGE);
                        if(imageFragment == null){
                            ImageFragment newImageFragment = new ImageFragment();
                            newImageFragment.addItem(fileUriString);

                        }else{
                            imageFragment.addItem(fileUriString);
                        }
                        alertDialog.cancel();
                    }
                }, 5000);
            }
        }
    }

    @Override
    public void onImageSelected(int count) {
        if( mTabHost.getTabWidget().getChildAt(1) != null){
            if(count != 0){
                ((TextView) mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getResources().getString(R.string.images_tab) + "  " + count);

            }else{
                ((TextView)mTabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title)).setText(getResources().getString(R.string.image));
            }
        }else {
            if(count != 0){
                ((TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.images_tab) + "  "  + count);

            }else{
                ((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.image));
            }
        }
    }

    public void onVideoSelected(int count){
        if(count != 0){
            ((TextView) mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.videos_tab) + "  "  + count);

        }else{
            ((TextView)mTabHost.getTabWidget().getChildAt(0).findViewById(android.R.id.title)).setText(getResources().getString(R.string.video));
        }
    }

    public void onVideoSelected(ArrayList<String> items) {

    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void setHeaderBarCameraBackground(Drawable drawable) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            headerBarCamera.setBackgroundDrawable(drawable);
        } else {
            headerBarCamera.setBackground(drawable);
        }
    }

    public int convertDipToPixels(float dips){
        return (int) (dips * HomeFragmentActivity.this.getResources().getDisplayMetrics().density + 0.5f);
    }

}
