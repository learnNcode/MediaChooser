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


package com.learnNcode.mediachooser;

public class MediaChooserConstants {

	/**
	 * folder name on which captured photo & video are saved on sd card.
	 */
	public static String folderName = "learnNcode";

	/**
	 * No of item that can be selected. Default is 10.
	 */
	public static int MAX_MEDIA_LIMIT = 10;

	/**
	 * Selected media file count.
	 */
	public static int SELECTED_MEDIA_COUNT  = 0;


	/**
	 * Video file selected broadcast action.
	 */
	public static final String VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER = "lNc_videoSelectedAction"; 
	
	/**
	 *  Image file selected broadcast action.
	 */
	public static final String IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER = "lNc_imageSelectedAction"; 

	
	
	public static final int BUCKET_SELECT_IMAGE_CODE = 1000;
	public static final int BUCKET_SELECT_VIDEO_CODE = 2000;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	public static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;



}
