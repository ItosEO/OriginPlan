package com.itos.xplan.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetUtils {

    //Post请求1，参数俩
    public static String Post(String ur, String byteString) {
        String fh = "";
        try {
            URL url = new URL(ur);
            HttpURLConnection HttpURLConnection = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setReadTimeout(5000);
            HttpURLConnection.setRequestMethod("POST");
            OutputStream outputStream = HttpURLConnection.getOutputStream();
            outputStream.write(byteString.getBytes());
            BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(HttpURLConnection.getInputStream()));
            String String = "";
            StringBuffer StringBuffer = new StringBuffer();
            while ((String = BufferedReader.readLine()) != null) {
                StringBuffer.append(String);
            }
            fh = StringBuffer.toString();

        } catch (IOException e) {
            Log.d("post", String.valueOf(e));
            fh="666";
        }
        return fh;
    }

    //Post请求1，无参数
    public static String Post(String string) {
        String a="";
        try {
            URL url = new URL(string);
            HttpURLConnection HttpURLConnection = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setReadTimeout(5000);
            HttpURLConnection.setRequestMethod("POST");
            BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(HttpURLConnection.getInputStream()));
            String String = "";
            StringBuffer StringBuffer = new StringBuffer();
            while ((String = BufferedReader.readLine()) != null) {
                StringBuffer.append(String);
            }
            a = StringBuffer.toString();
        }catch (Exception ignored){}
        return a;
    }

    public static String Get(String string) throws IOException {
        URL url = new URL(string);
        HttpURLConnection HttpURLConnection = (HttpURLConnection) url.openConnection();
        HttpURLConnection.setReadTimeout(5000);
        HttpURLConnection.setRequestMethod("GET");
        BufferedReader BufferedReader = new BufferedReader(new InputStreamReader(HttpURLConnection.getInputStream()));
        String String = "";
        StringBuffer StringBuffer = new StringBuffer();
        while ((String = BufferedReader.readLine()) != null) {
            StringBuffer.append(String);
        }
        return StringBuffer.toString();
    }



}
