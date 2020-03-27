package com.artemissoftware.videoplayer;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

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

        SimpleDateFormat sd = new SimpleDateFormat("yymmhh");
        String date = sd.format(new Date());
        String name = "video" + date + ".mp4";

        try {

            String rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "My_Video";
            File rootFile = new File(rootDir);
            boolean dirAvailable = rootFile.mkdir();

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


/*
        @Override
    protected String doInBackground(String... sUrl) {

        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            java.net.URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();
            fileN = "FbDownloader_" + UUID.randomUUID().toString().substring(0, 10) + ".mp4";
            File filename = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME, fileN);
            output = new FileOutputStream(filename);

            byte data[] = new byte[4096];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

        }
        catch (Exception e) {
            return e.toString();
        }
        finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            }
            catch (IOException ignored) {

            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();

        LayoutInflater dialogLayout = LayoutInflater.from(MainActivity.this);
        View DialogView = dialogLayout.inflate(R.layout.progress_dialog, null);
        downloadDialog = new Dialog(MainActivity.this, R.style.CustomAlertDialog);
        downloadDialog.setContentView(DialogView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(downloadDialog.getWindow().getAttributes());
        lp.width = (getResources().getDisplayMetrics().widthPixels);
        lp.height = (int)(getResources().getDisplayMetrics().heightPixels*0.65);
        downloadDialog.getWindow().setAttributes(lp);

        final Button cancel = (Button) DialogView.findViewById(R.id.cancel_btn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopping the Asynctask
                cancel(true);
                downloadDialog.dismiss();

            }
        });

        downloadDialog.setCancelable(false);
        downloadDialog.setCanceledOnTouchOutside(false);
        bnp = (NumberProgressBar)DialogView.findViewById(R.id.number_progress_bar);
        bnp.setProgress(0);
        bnp.setMax(100);
        downloadDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        bnp.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {

        mWakeLock.release();
        downloadDialog.dismiss();

        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

        MediaScannerConnection.scanFile(MainActivity.this, new String[]{Environment.getExternalStorageDirectory().getAbsolutePath() + Constants.FOLDER_NAME + fileN}, null,
        new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String newpath, Uri newuri) {
                Log.i("ExternalStorage", "Scanned " + newpath + ":");
                Log.i("ExternalStorage", "-> uri=" + newuri);
            }
        });

    }
    */
}
