package com.learnncode.mediachooser;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.learnncode.mediachooser.adapter.BucketGridAdapter;
import com.learnncode.mediachooser.adapter.GridViewAdapter;
import com.learnncode.mediachooser.fragment.BucketVideoFragment;
import com.learnncode.mediachooser.fragment.VideoFragment;



public class GalleryCache {
	private LruCache<String, Bitmap> mBitmapCache;
	private ArrayList<String> mCurrentTasks;
	private int mMaxWidth;
    private int mMaxHeight;
    private float mRelation;

	public GalleryCache(int size, int maxWidth, int maxHeight) {
		mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mRelation = (float) maxWidth / maxHeight;

		mBitmapCache = new LruCache<String, Bitmap>(size) {
			@Override
			protected int sizeOf(String key, Bitmap b) {
				// Assuming that one pixel contains four bytes.
				return b.getHeight() * b.getWidth() * 4;
			}
		};

		mCurrentTasks = new ArrayList<String>();
	}

	private void addBitmapToCache(String key, Bitmap bitmap) {
		if (getBitmapFromCache(key) == null) {
			mBitmapCache.put(key, bitmap);
		}
	}

	private Bitmap getBitmapFromCache(String key) {
		return mBitmapCache.get(key);
	}

	/**
	 * Gets a bitmap from cache. <br/>
	 * <br/>
	 * If it is not in cache, this method will: <br/>
	 * <b>1:</b> check if the bitmap url is currently being processed in the
	 * BitmapLoaderTask and cancel if it is already in a task (a control to see
	 * if it's inside the currentTasks list). <br/>
	 * <b>2:</b> check if an internet connection is available and continue if
	 * so. <br/>
	 * <b>3:</b> download the bitmap, scale the bitmap if necessary and put it
	 * into the memory cache. <br/>
	 * <b>4:</b> Remove the bitmap url from the currentTasks list. <br/>
	 * <b>5:</b> Notify the ListAdapter.
	 * 
	 * @param mainActivity
	 *            - Reference to activity object, in order to call
	 *            notifyDataSetChanged() on the ListAdapter.
	 * @param imageKey
	 *            - The bitmap url (will be the key).
	 * @param imageView
	 *            - The ImageView that should get an available bitmap or a
	 *            placeholder image.
	 * @param isScrolling
	 *            - If set to true, we skip executing more tasks since the user
	 *            probably has flinged away the view.
	 */
	public void loadBitmap(Fragment mainActivity, String imageKey,
			ImageView imageView, boolean isScrolling) {
		final Bitmap bitmap = getBitmapFromCache(imageKey);
		

		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setAdjustViewBounds(true);
		} else {
			imageView.setImageResource(R.drawable.ic_loading);
			//			imageView.setImageResource(R.drawable.transprent_drawable);
			if (!isScrolling && !mCurrentTasks.contains(imageKey)) {
				if(mainActivity instanceof VideoFragment){
					BitmapLoaderTask task = new BitmapLoaderTask(imageKey, ((VideoFragment)mainActivity).getAdapter());
					task.execute();

				}else if(mainActivity instanceof BucketVideoFragment){
					BitmapLoaderTask task = new BitmapLoaderTask(imageKey, ((BucketVideoFragment)mainActivity).getAdapter());
					task.execute();
				}
			}
		}
	}

	private class BitmapLoaderTask extends AsyncTask<Void, Void, Bitmap> {
		private GridViewAdapter mAdapter;
		private BucketGridAdapter mBucketGridAdapter;
		private String mImageKey;

		public BitmapLoaderTask(String imageKey, GridViewAdapter adapter) {
			mAdapter = adapter;
			mImageKey = imageKey;
		}

		public BitmapLoaderTask(String imageKey, BucketGridAdapter adapter) {
			mBucketGridAdapter = adapter;
			mImageKey = imageKey;
		}

		@Override
		protected void onPreExecute() {
			mCurrentTasks.add(mImageKey);
		}

        //http://stackoverflow.com/questions/8112715/how-to-crop-bitmap-center-like-imageview
        public Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();

            // Compute the scaling factors to fit the new height and width, respectively.
            // To cover the final image, the final scaling will be the bigger
            // of these two.
            float xScale = (float) newWidth / sourceWidth;
            float yScale = (float) newHeight / sourceHeight;
            float scale = Math.max(xScale, yScale);

            // Now get the size of the source bitmap when scaled
            float scaledWidth = scale * sourceWidth;
            float scaledHeight = scale * sourceHeight;

            // Let's find out the upper left coordinates if the scaled bitmap
            // should be centered in the new size give by the parameters
            float left = (newWidth - scaledWidth) / 2;
            float top = (newHeight - scaledHeight) / 2;

            // The target rectangle for the new, scaled version of the source bitmap will now
            // be
            RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

            // Finally, we create a new bitmap of the specified size and draw our new,
            // scaled bitmap onto it.
            Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
            Canvas canvas = new Canvas(dest);
            canvas.drawBitmap(source, null, targetRect, null);

            return dest;
        }

		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap bitmap = null;
			try {
				bitmap = ThumbnailUtils.createVideoThumbnail(mImageKey, Thumbnails.FULL_SCREEN_KIND);

				if (bitmap != null) {
/*
                    int originalHeight = bitmap.getHeight();
                    int originalWidth = bitmap.getWidth();
                    if(originalHeight > originalWidth) {
                        int scaled = originalHeight * mMaxWidth / originalWidth;
                        bitmap = Bitmap.createScaledBitmap(bitmap, mMaxWidth, scaled, true);
                    } else {
                        int scaled = originalWidth * mMaxHeight / originalHeight;
                        bitmap = Bitmap.createScaledBitmap(bitmap, scaled, mMaxHeight, true);
                    }
                    */
                    bitmap = scaleCenterCrop(bitmap, mMaxWidth, mMaxHeight);

					addBitmapToCache(mImageKey, bitmap);
					return bitmap;
				}
				return null;
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap param) {
			mCurrentTasks.remove(mImageKey);
			if (param != null) {
				if(mAdapter != null){
					mAdapter.notifyDataSetChanged();
				}else{
					mBucketGridAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	public void clear() {
		mBitmapCache.evictAll();
	}



}
