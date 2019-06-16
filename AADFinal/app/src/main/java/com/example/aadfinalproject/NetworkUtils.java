package com.example.aadfinalproject;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NetworkUtils {
    static String getLabelInfo(String queryString,double[] canvasSize,String XString,String YString,String TString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String labelJSONString = null;

        ArrayList<String> x = new ArrayList<String>();
        ArrayList<String> y = new ArrayList<String>();
        ArrayList<String> t = new ArrayList<String>();

        x.add("473.09999999999997");
        x.add("474.09999999999997");
        x.add("475.09999999999997");

        y.add("231.7");
        y.add("246.7");
        y.add("265.7");

        t.add("2");
        t.add("35");
        t.add("132");

        try {
            Uri builtURI = Uri.parse(queryString);
            URL requestURL = new URL(builtURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("POST");


            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            /*
            String bodyString = "{\"input_type\":0,\"requests\":[{\"language\":\"autodraw\",\"writing_guide\":" +
                    "{\"width\":847.1999999999999,\"height\":635.4}," +
                    "\"ink\":[[" +
                    "[473.09999999999997,473.09999999999997,473.09999999999997,473.09999999999997,473.09999999999997,474.09999999999997,474.09999999999997,474.09999999999997,474.09999999999997,474.09999999999997,474.09999999999997,474.09999999999997,475.09999999999997,475.09999999999997,475.09999999999997,475.09999999999997]," +
                    "[231.7,233.7,236.7,239.7,241.7,246.7,249.7,251.7,254.7,256.7,259.7,260.7,262.7,263.7,264.7,265.7]," +
                    "[2,9,17,19,29,35,45,52,63,68,79,84,96,101,116,132]" +
                    "]]}]}";
            */

            String bodyString = "{\"input_type\":0,\"requests\":[{\"language\":\"autodraw\",\"writing_guide\":" +
                    "{\"width\":" +
                    canvasSize[0] +
                    ",\"height\":" +
                    canvasSize[1] +
                    "}," +
                    "\"ink\":[[" +
                    XString + "," + YString + "," + TString +
                    "]]}]}";
            /*
            try {
                JSONObject ss = new JSONObject(bodyString);
                OutputStream os = urlConnection.getOutputStream();
                os.write(ss.toString().getBytes("utf-8"));
                os.flush();
                os.close();
            } catch (JSONException err) {

            }

            */

            OutputStream os = urlConnection.getOutputStream();
            os.write(bodyString.getBytes("utf-8"));
            os.flush();
            os.close();



            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream is = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                labelJSONString = parse_ans(sb.toString());
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (urlConnection != null){urlConnection.disconnect();}
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {e.printStackTrace();}
            }
        }
        Log.e("AAAAANNNNNSSSSS", labelJSONString);
        return labelJSONString;
    }

    static String parse_ans(String s){

        int left_bracket_count = 0;
        String target = "";
        int start = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '[') {
                left_bracket_count++;
            }
            if (left_bracket_count == 4) {
                start = i + 1;
                break;
            }
        }

        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == ']') {
                break;
            }
            target += s.charAt(i);
        }

        String prob = "";

        start = s.indexOf("SCORESINKS");
        int end = s.indexOf("Service_Recognize");

        for (int i = start; i < end; i++) {
            prob += s.charAt(i);
        }

        String[] key = new String[20];
        String[] P = new String[20];

        String[] split_value = prob.split(",");
        split_value[0] = split_value[0].replace("SCORESINKS", "");
        for (int i = 0; i < split_value.length; i++) {
            split_value[i] = split_value[i].replaceAll("[^dA-Za-z0-9.]", "");
            if ((i & 1) == 0) {
                key[i / 2] = split_value[i];
            } else {
                P[i / 2] = split_value[i];
            }
        }

        String label_p = "";

        for (int i = 0; i < 20; i++) {
            //label_p += key[i] + ": " + String.valueOf(100 - Float.parseFloat(P[i])) + "%\n";
            label_p += key[i] + ": " + String.valueOf(Float.parseFloat(P[i])) + "\n";
            //Log.e("PPPPPPP", key[i] + ": " + P[i]);
        }



        return label_p;


    }
}