package com.example.asynctasks_threads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textViewProgressBar;
    TextView textViewSeekProgress;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        TextView textViewAverage = findViewById(R.id.textViewAverage);
        TextView textViewDisplayAverage = findViewById(R.id.textViewDisplayAverage);
        TextView textViewProgressBar = findViewById(R.id.textViewProgressBar);
        textViewSeekProgress = findViewById(R.id.textViewSeekProgress);

        progressBar.setVisibility(View.INVISIBLE);
        textViewAverage.setVisibility(View.INVISIBLE);

        progressBar.setMax(20);
        progressBar.setProgress(10);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekProgress.setText(String.valueOf(progress) + " Times");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


    }

}