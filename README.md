Media Chooser
======================

Library to browse & select videos and images from disk.


Screenshot
=========


![Video items](https://dl.dropboxusercontent.com/u/61919232/learnNcode/MediaChooser/video_selected.png "File view")
<p>
![Folder image items](https://dl.dropboxusercontent.com/u/61919232/learnNcode/MediaChooser/bucket_image.png "Folder view")



setup
===================
Add following permission to your applications manifest file.

     <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

Add following code to the application node of your application's manifest file

    <activity
        android:name="com.learnNcode.mediachooser.activity.HomeFragmentActivity"
        android:configChanges="orientation|screenSize"
        android:screenOrientation="portrait" />
    <activity
        android:name="com.learnNcode.mediachooser.activity.BucketHomeFragmentActivity"
        android:configChanges="orientation|screenSize"
        android:screenOrientation="portrait" />

What does this library do ?
==================

Useful library for selecting images and videos from sd-card. The library can be used to disply images & videos in  file view or folder view.
File view shows all files whereas Folder view shows files categorized.
All items are sorted according to date-time with latest item showing first.




Usage
=====

    To display images and videos according to:
    ================================================
     1]Folders 
    
        Intent intent = new Intent(MainActivity.this, BucketHomeFragmentActivity.class);
        startActivity(intent);
               
     2]Files          
        Intent intent = new Intent(MainActivity.this, HomeFragmentActivity.class);
    	startActivity(intent);
        
    To get list of selected images and videos :
    =================================================
    1] For images you have to register a broadcast with "MediaChooserConstants.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER" action.
    Example:
    IntentFilter imageIntentFilter = new IntentFilter(MediaChooserConstants.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
    	registerReceiver(imageBroadcastReceiver, imageIntentFilter);
        
    2] For videos you have to register a broadcast with "MediaChooserConstants.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER" action.
    Example:
    IntentFilter videoIntentFilter = new IntentFilter(MediaChooserConstants.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
    	registerReceiver(videoBroadcastReceiver, videoIntentFilter);
        
Note
==================
    You can define number of image/video selection by:
     MediaChooserConstants.MAX_MEDIA_LIMIT = 20;  //default set to 10.
    
    You can get total selected count by:
    int totalCount = MediaChooserConstants.SELECTED_MEDIA_COUNT;

Check the attached demo sample app.
    
Acknowledgement
==============
[Picasso jar](http://square.github.io/picasso/)
    
License
======

    Copyright 2013 learnNcode (learnncode@gmail.com)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

Thank You
========

  If you like our work say a hi :)
  <br>
  Happy Coding Happy Learning.
