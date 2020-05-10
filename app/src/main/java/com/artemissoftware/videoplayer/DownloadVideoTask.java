package com.artemissoftware.videoplayer;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artemissoftware.videoplayer.util.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * DownloadVideoTask for downloding video from URL
 */
public class DownloadVideoTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = "DownloadVideoTask";

    private Context context;
    private ProgressBar progressBar;
    private String error;
    /*

    private PowerManager.WakeLock mWakeLock;
    private NumberProgressBar bnp;
*/

    public DownloadVideoTask(Context context, ProgressBar progressBar) {
        this.context = context;
        this.error = null;
        this.progressBar = progressBar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... url) {

        downloadfile(url[0]);
        return null;
    }




    private void downloadfile(String vidurl) {
        //vidurl = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
        String date = sd.format(new Date());
        String name = "video" + date + ".mp4";

        try {

            String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + Resources.VIDEO_DOWNLOAD_DIRECTORY;
            File rootFile = new File(rootDir);
            boolean dirAvailable = false;

            if(rootFile.exists() && rootFile.isDirectory()) {
                dirAvailable = true;
            }
            else {
                dirAvailable = rootFile.mkdir();
            }

            if(dirAvailable == true) {

                FileOutputStream fileOutputStream = new FileOutputStream(new File(rootFile, name));

                byte[] buffer = new byte[1024];

                URL url = new URL(vidurl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    error = "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }
                else {
                    long total = 0;
                    int fileLength = connection.getContentLength();
                    progressBar.setMax(fileLength);


                    InputStream inputStream = connection.getInputStream();

                    int count = 0;
                    while ((count = inputStream.read(buffer)) > 0) {

                        if (isCancelled()) {
                            inputStream.close();
                            error = "Download cancelled";
                            return;
                        }
                        total += count;
                        if (fileLength > 0)
                            publishProgress((int) (total * 100 / fileLength));

                        fileOutputStream.write(buffer, 0, count);

                    }

                    fileOutputStream.close();
                }
            }
            else{
                error = "Failed to create directory";
            }
        }
        catch (IOException e) {
            Log.e(TAG, "Error: " + e.toString());
        }
        catch (Exception e) {
            Log.e(TAG, "Error: " + e.toString());
        }
    }


    @Override
    protected void onProgressUpdate(Integer[] values) {

        progressBar.setProgress(values[0]);
        super.onProgressUpdate(values);
    }



    @Override
    protected void onPostExecute(String result) {

        if (error != null)
            Toast.makeText(context, "Download error: " + error, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
    }

}
