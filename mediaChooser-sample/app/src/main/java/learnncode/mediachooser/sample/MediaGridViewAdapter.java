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


package learnncode.mediachooser.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MediaGridViewAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private List<String> mediaFilePathList;
    LayoutInflater viewInflater;
    private int mWidth;


    public MediaGridViewAdapter(Context context, int resource, List<String> filePathList) {
        super(context, resource, filePathList);
        mediaFilePathList = filePathList;
        mContext = context;
        viewInflater = LayoutInflater.from(mContext);
    }

    public int getCount() {
        return mediaFilePathList.size();
    }

    @Override
    public String getItem(int position) {
        return mediaFilePathList.get(position);
    }


    public void addAll(List<String> mediaFile) {
        if (mediaFile != null) {
            int count = mediaFile.size();
            for (int i = 0; i < count; i++) {
                if (!mediaFilePathList.contains(mediaFile.get(i))) {
                    mediaFilePathList.add(mediaFile.get(i));
                }
            }
        }
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            convertView = viewInflater.inflate(R.layout.view_grid_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFromGridItemRowView);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        LayoutParams imageParams = (LayoutParams) holder.imageView.getLayoutParams();
        imageParams.width = mWidth / 2;
        imageParams.height = mWidth / 2;

        holder.imageView.setLayoutParams(imageParams);

        File mediaFile = new File(mediaFilePathList.get(position));

        if (mediaFile.exists()) {
            if (mediaFile.getPath().contains("mp4") || mediaFile.getPath().contains("wmv") ||
                    mediaFile.getPath().contains("avi") || mediaFile.getPath().contains("3gp")) {
                holder.imageView.setImageBitmap(null);

                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ic_video_selected_from_media_chooser_header_bar));
                } else {
                    holder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.ic_video_selected_from_media_chooser_header_bar));
                }

            } else {
                Options options = new Options();
                options.inPurgeable = true;
                options.inSampleSize = 2;
                Bitmap myBitmap = BitmapFactory.decodeFile(mediaFile.getAbsolutePath(), options);
                holder.imageView.setImageBitmap(myBitmap);
            }

            holder.nameTextView.setText(mediaFile.getName());

        }

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView nameTextView;
    }

}