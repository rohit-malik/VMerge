package com.example.nitinmalik.uploading_video;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter {

    private List<Video> videoList;
    private Context context;
    private String event_name;

    public VideoAdapter(Context context, List<Video> videoList, String event_name) {
        this.context = context;
        this.videoList = videoList;
        this.event_name = event_name;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_video, parent, false);
        // set the view's size, margins, paddings and layout parameters
        VideoAdapter.MyViewHolder vh = new VideoAdapter.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1,final int position) {
        VideoAdapter.MyViewHolder holder = (VideoAdapter.MyViewHolder) holder1;
        if((position%2)==0) {
            (holder.parent).setBackgroundColor(Color.parseColor("#2f2f2f"));
        }
        else
        {
            (holder.parent).setBackgroundColor(Color.parseColor("#434343"));
        }
        final Video video = videoList.get(position);
        //holder.ViewVideo_ID.setText("Video ID: " + String.valueOf(video.getVideo_ID()));
        holder.ViewVideo_name.setText(video.getVideo_name());
        //holder.ViewVideo_desc.setText(video.getVideo_desc());
        Bitmap bitmap = null;
        try {
            bitmap = retriveVideoFrameFromVideo("http://172.26.1.221/AndroidUploadImage/" + event_name + "/" + video.getVideo_name());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        holder.ViewVideo_thumbnail.setImageBitmap(bitmap);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, videoList.get(position).getVideo_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, StreamVideo.class);
                intent.putExtra("video_name", video.getVideo_name());
                intent.putExtra("event_name", event_name);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ViewVideo_ID, ViewVideo_name, ViewVideo_desc;// init the item view's
        ImageView ViewVideo_thumbnail;
        RelativeLayout parent;
        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            //ViewVideo_ID = (TextView) itemView.findViewById(R.id.video_ID);
            ViewVideo_name = (TextView) itemView.findViewById(R.id.video_name);
            //ViewVideo_desc = (TextView) itemView.findViewById(R.id.video_desc);
            ViewVideo_thumbnail = itemView.findViewById(R.id.thumbnail);
            parent = itemView.findViewById(R.id.video_rel_view);
        }
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
