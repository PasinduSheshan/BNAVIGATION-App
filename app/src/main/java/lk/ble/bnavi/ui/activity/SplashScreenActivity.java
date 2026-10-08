package lk.ble.bnavi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import lk.ble.bnavi.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final long SPLASH_SCREEN =3000;

    private Animation logoAnimation, contextAnimation;

    private ImageView logo;
    private TextView context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logoAnimation = AnimationUtils.loadAnimation(this, R.anim.sp_logo_anim);
        contextAnimation = AnimationUtils.loadAnimation(this, R.anim.sp_context_anim);

        logo = findViewById(R.id.splashScreenLogo);
        context = findViewById(R.id.splashScreenContext);

        logo.setAnimation(logoAnimation);
        context.setAnimation(contextAnimation);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(this,HomeActivity.class));
            finish();
        },SPLASH_SCREEN);
    }
}