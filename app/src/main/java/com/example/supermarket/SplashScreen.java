package com.example.supermarket;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.supermarket.Auth.LoginActivity;

public class SplashScreen extends AppCompatActivity {

    private int waktu_loading=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //setelah loading maka akan langsung berpindah ke home activity
                Intent home=new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(home);
                finish();

            }
        },waktu_loading);
    }

}
