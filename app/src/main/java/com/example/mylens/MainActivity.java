package com.example.mylens;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static int SPLASH_SCREEN = 3000;
Animation top,bottom;
ImageView i2,i3,i4,i5,i6;
TextView t1,t2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        top = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom = AnimationUtils.loadAnimation(this,R.anim.botton_animation);


        i2=findViewById(R.id.i2);
        i3=findViewById(R.id.i3);
        i5=findViewById(R.id.i5);
        i6=findViewById(R.id.i6);


        t1=findViewById(R.id.t1);
        t2=findViewById(R.id.t2);

        i2.setAnimation(top);
        i3.setAnimation(top);
        i5.setAnimation(top);
        i6.setAnimation(top);
        t1.setAnimation(bottom);
        t2.setAnimation(bottom);

new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {
        Intent intent = new Intent(MainActivity.this,Dashboard.class);
        startActivity(intent);
        finish();
    }
},SPLASH_SCREEN);




    }
}