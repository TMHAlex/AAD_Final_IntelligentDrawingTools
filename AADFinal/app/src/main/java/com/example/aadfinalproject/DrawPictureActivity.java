package com.example.aadfinalproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//For Auto draw
public class DrawPictureActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private  PaintView paintView;
    private TextView resultLabel, identifyResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_picture);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultLabel = findViewById(R.id.resultLabel);
        identifyResult = findViewById(R.id.identifyResult);
        paintView = (PaintView) findViewById(R.id.paintView);

        if(getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0,null,this);
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //開始做辨識的工作
                double canvasSize[] = {paintView.getWidth(), paintView.getHeight()};
                //Log.e("畫布", "width = "+canvasSize[0]+"   height="+canvasSize[1]);
                ArrayList<ArrayList<Float>> graphData = paintView.GetXYTimePoint();
                for(int i = 0; i < graphData.size(); i++) {
                    ArrayList<Float> temp = graphData.get(i);
                    for(int j = 0; j < temp.size(); j++) {
                        if(i == 2) {
                            //Log.e("Graph Data", i + "-" + j + "  " + temp.get(j).toString());
                        }
                    }
                }
                //辨識完，結果要顯示
                resultLabel.setVisibility(View.VISIBLE);
                identifyResult.setVisibility(View.VISIBLE);

                predict_draw(canvasSize,graphData);



            }
        });

        //在辨識之前先不用出現:
        resultLabel.setVisibility(View.INVISIBLE);
        identifyResult.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                paintView.clear();
                identifyResult.setText("");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void predict_draw(double[] canvasSize,ArrayList<ArrayList<Float>> graphData){
        //Log.e("長寬", "width = "+canvasSize[0]+"   height="+canvasSize[1]);
        //Log.e("x", graphData.get(0).toString());
        //Log.e("y", graphData.get(1).toString());
        //Log.e("t", graphData.get(2).toString());

        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnected()){
            String queryString = "https://inputtools.google.com/request?ime=handwriting&app=autodraw&dbg=1&cs=1&oe=UTF-8";

            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);

            queryBundle.putDoubleArray("canvasSize", canvasSize);
            queryBundle.putString("XString", graphData.get(0).toString());
            queryBundle.putString("YString", graphData.get(1).toString());
            queryBundle.putString("TString", graphData.get(2).toString());

            getSupportLoaderManager().restartLoader(0, queryBundle, this);

        }else {
            //textView.setText("Check your internet connection and try again.");
        }

    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        String queryString = "";
        double canvasSize[] = new double[2];
        String XString = "";
        String YString = "";
        String TString = "";

        if (args != null) {
            queryString = args.getString("queryString");
            canvasSize = args.getDoubleArray("canvasSize");
            XString = args.getString("XString");
            YString = args.getString("YString");
            TString = args.getString("TString");
        }

        return new LabelLoader(this, queryString,canvasSize,XString,YString,TString);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        identifyResult.setText(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}