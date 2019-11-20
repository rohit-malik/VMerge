package com.example.nitinmalik.uploading_video;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class StreamMashup extends AppCompatActivity {


    VideoView videoView;
    ProgressDialog progressDialog;
    String video_url = "http://172.26.1.221/AndroidUploadImage/";
    String event_name;
    String video_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream_mashup);
        Intent i = getIntent();
        event_name = i.getStringExtra("event_name");
        video_name = i.getStringExtra("video_name");
        video_url = video_url + event_name + "/mashup" + "/" + video_name;
        Button download_mashup = findViewById(R.id.button_download_mashup);
        download_mashup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(video_url);
            }
        });

        MyTask task = new MyTask();
        task.execute(video_url);

        videoView = (VideoView) findViewById(R.id.VideoViewMash);
        progressDialog = new ProgressDialog(StreamMashup.this);
        progressDialog.setTitle("streaming");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        MediaController mediaController = new MediaController(StreamMashup.this);
        mediaController.setAnchorView(videoView);
        Uri uri = Uri.parse(video_url);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        progressDialog.dismiss();
        videoView.start();
    }

    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            boolean bResponse = result;
            if (bResponse==true)
            {
                //Toast.makeText(MainActivity.this, "File exists!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(StreamMashup.this, "Mashup is being created. Please wait...", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void download(String video_url) {
        Downback DB = new Downback();
        DB.execute(video_url);

    }


    private class Downback extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            final String vidurl = strings[0];
            downloadfile(vidurl);
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(StreamMashup.this, "Mashup Downloaded Successfully", Toast.LENGTH_LONG).show();
        }

    }

    private void downloadfile(String vidurl) {

        String name = event_name + "mashup" + ".mp4";

        try {
            String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + File.separator + event_name;
            File rootFile = new File(rootDir);
            rootFile.mkdir();
            URL url = new URL(vidurl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(rootFile,
                    name));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
//            finish();
//            startActivity(getIntent());
        } catch (IOException e) {
            Log.d("Error....", e.toString());
        }


    }
}


