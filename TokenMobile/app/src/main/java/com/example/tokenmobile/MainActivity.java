package com.example.tokenmobile;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.SecureRandom;
import android.util.Base64;

public class MainActivity extends AppCompatActivity {
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe

    public static String generateNewToken() {
        byte[] randomBytes = new byte[12];
        secureRandom.nextBytes(randomBytes);
        return  Base64.encodeToString(randomBytes, Base64.NO_WRAP);
    }
    TextView usernameTextView;
    TextView passwordTextView;
    Button loginbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameTextView =(TextView) findViewById(R.id.username);
        passwordTextView =(TextView) findViewById(R.id.password);
        loginbtn = (Button) findViewById(R.id.button);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameTextView.getText().toString();
                String password = passwordTextView.getText().toString();
                String tokenGenerated = generateNewToken();
                ((TextView)findViewById(R.id.token)).setText(tokenGenerated);
                Log.d("MyApp","Prije poziva");
                new  TokenActivity(username,password, tokenGenerated).execute(username,password,tokenGenerated);
                Log.d("MyApp","Poslije poziva");
            }
        });
    }

    /*private void writeToken(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray array = new JSONArray();
                            for(int i = 0; i < array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                            }
                        }catch(Exception e){
                        }
                    }
                }, new Response.ErrorListener(){
                   @Override
                   public void onErrorResponse(VolleyError error){
                       //Toast.makeText()
                   }
                }
        );
    }*/
}