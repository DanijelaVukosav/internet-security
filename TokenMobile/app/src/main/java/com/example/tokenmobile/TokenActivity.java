package com.example.tokenmobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.Security;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import javax.net.ssl.HttpsURLConnection;


public class TokenActivity extends AsyncTask{

    private String username,password,token;

    public TokenActivity(String username,String password,String token) {
        this.username = username;
        this.password=password;
        this.token = token;
    }

    private String sha256(final String base) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected Object doInBackground(Object[] arg0) {
        Log.d("MyApp","U metodi");
        try{
            String username = (String)arg0[0].toString();
            String password = sha256((String)arg0[1].toString());

            Log.d("MyApp",password);
            String token = (String)arg0[2].toString();
            String link="http://10.1.1.138:8080/Token/Controller";
            String data  = URLEncoder.encode("username", "UTF-8") + "=" +
                    URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("token", "UTF-8") + "=" +
                    URLEncoder.encode(token, "UTF-8");

            URL url = new URL(link);
            URLConnection conn =url.openConnection();
            //HttpURLConnection httpsConn = (HttpURLConnection)
           // httpsConn.setAllowUserInteraction(false);
            //httpsConn.setInstanceFollowRedirects(true);
           // httpsConn.setRequestMethod("POST");
            //httpsConn.connect();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write( data );
            wr.flush();

            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }

            return sb.toString();
        } catch(Exception e){
            Log.d("MyApp","Exception "+e.getMessage());
            return new String("Exception: " + e.getMessage());
        }

    }

    protected void onPreExecute(){
    }

    protected void onPostExecute(String result){
        Log.d("MyApp",result);
    }
}