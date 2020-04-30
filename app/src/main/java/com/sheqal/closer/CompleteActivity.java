package com.sheqal.closer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompleteActivity extends AppCompatActivity {

    @BindView(R.id._completedSetup)
    LottieAnimationView _lottie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        ButterKnife.bind(this);
        _changeActivity();

    }

    public void _changeActivity(){
        _lottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Intent intent = new Intent(CompleteActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
