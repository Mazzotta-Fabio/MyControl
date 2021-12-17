package com.example.mycontrol.interfacciagrafica;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.example.mycontrol.R;

public class PreActivityMain extends Activity {

    private ProgressBar progressBar;
    private Intent intentActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intentActivity=new Intent();
        progressBar=(ProgressBar)findViewById(R.id.progressTime);
        new HelpThreadActivity().execute();
    }

    private class HelpThreadActivity extends AsyncTask<Void,Integer,Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                for (int i = 0; i < 4; i++) {
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setProgress(View.GONE);
            intentActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intentActivity);
        }
    }

    public void onPause(){
        super.onPause();
        finish();
    }
}