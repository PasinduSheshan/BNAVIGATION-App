package lk.ble.bnavi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import lk.ble.bnavi.R;

public class HomeActivity extends AppCompatActivity {

    private View lecHall1, lecHall2, aboutPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lecHall1 = findViewById(R.id.lectureHall1);
        lecHall2 = findViewById(R.id.lectureHall2);
        aboutPanel = findViewById(R.id.aboutPanel);

        lecHall1.setOnClickListener(v -> {
            startActivity(new Intent(this, LectureHall1Activity.class));
        });

        lecHall2.setOnClickListener(v -> {
            startActivity(new Intent(this, LectureHall2Activity.class));
        });

        aboutPanel.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });
    }

}