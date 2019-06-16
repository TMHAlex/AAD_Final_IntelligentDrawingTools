package com.example.aadfinalproject;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.support.annotation.Nullable;


public class LabelLoader extends AsyncTaskLoader<String> {

    private String mQueryString;

    private  double[] mcanvasSize;
    private String mXString;
    private String mYString;
    private String mTString;


    LabelLoader(Context context, String queryString, double[] canvasSize,String XString,String YString,String TString) {
        super(context);
        mQueryString = queryString;
        mcanvasSize = canvasSize;
        mXString = XString;
        mYString = YString;
        mTString = TString;


    }

    @Nullable
    @Override
    public String loadInBackground() {
        return NetworkUtils.getLabelInfo(mQueryString,mcanvasSize,mXString,mYString,mTString);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}