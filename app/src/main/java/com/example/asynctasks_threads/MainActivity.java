package com.example.asynctasks_threads;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView textViewProgressBar;
    TextView textViewSeekProgress;
    SeekBar seekBar;
    ArrayList<Double> numberList = new ArrayList<>();
    ArrayAdapter<Double> adapter;
    ListView listView;
    ProgressBar progressBar;
    TextView textViewDisplayAverage;
    int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        TextView textViewAverage = findViewById(R.id.textViewAverage);
        textViewDisplayAverage = findViewById(R.id.textViewDisplayAverage);
        textViewProgressBar = findViewById(R.id.textViewProgressBar);
        textViewSeekProgress = findViewById(R.id.textViewSeekProgress);

        progressBar.setVisibility(View.INVISIBLE);
        textViewAverage.setVisibility(View.INVISIBLE);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_expandable_list_item_1, android.R.id.text1, numberList);
        listView.setAdapter(adapter);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekProgress.setText(String.valueOf(progress) + " Times");

                findViewById(R.id.buttonAsync).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        textViewAverage.setVisibility(View.VISIBLE);
                        new MyAsyncTask().execute(progress);

                        progressBar.setMax(progress);
                        totalCount = progress;

                    }
                });
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        int getSeekProgress = seekBar.getProgress();
        Log.d("data", "onCreate: " + getSeekProgress);




    }

    class MyAsyncTask extends AsyncTask<Integer, Double, ArrayList<Double>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            adapter.clear();
            adapter.notifyDataSetChanged();
            progressBar.setProgress(0);
            textViewDisplayAverage.setText("");
            textViewProgressBar.setText("");
        }

        @Override
        protected void onProgressUpdate(Double... values) {
            super.onProgressUpdate(values);
            double num = values[0];
            adapter.add(num);
            adapter.notifyDataSetChanged();
            progressBar.setProgress(adapter.getCount());

            double sum = 0.0;
            for (Double entry : numberList){
                sum = sum + entry;
            }

            textViewProgressBar.setText(adapter.getCount() + "/" + String.valueOf(totalCount));

            textViewDisplayAverage.setText(String.valueOf(sum / (double) adapter.getCount()));

            Log.d("data", "onProgressUpdate: " + values);
            Log.d("data", "Total Count: " + totalCount);
        }

        @Override
        protected ArrayList<Double> doInBackground(Integer... integers) {
            try{
                int count = integers[0];
                for(int i = 0; i < count; i++){
                    double number = HeavyWork.getNumber();
                    publishProgress(number);
                }

            } catch (Exception e){
                Log.d("data", "doInBackground: AsycTask ArrayList method failed");
            }


            return null;
        }
    }

}
