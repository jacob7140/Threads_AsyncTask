package com.example.asynctasks_threads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    Button buttonAsync;
    Button buttonThread;
    Handler handler;
    ExecutorService threadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("AsyncTasks and Threads");

        progressBar = findViewById(R.id.progressBar);
        seekBar = findViewById(R.id.seekBar);
        TextView textViewAverage = findViewById(R.id.textViewAverage);
        textViewDisplayAverage = findViewById(R.id.textViewDisplayAverage);
        textViewProgressBar = findViewById(R.id.textViewProgressBar);
        textViewSeekProgress = findViewById(R.id.textViewSeekProgress);
        buttonAsync = findViewById(R.id.buttonAsync);
        buttonThread = findViewById(R.id.buttonThread);


        progressBar.setVisibility(View.INVISIBLE);
        textViewAverage.setVisibility(View.INVISIBLE);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<Double>(this, android.R.layout.simple_expandable_list_item_1, android.R.id.text1, numberList);
        listView.setAdapter(adapter);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Double newNumber = (Double)msg.obj;
                switch (msg.what){
                    case DoWork.STATUS_START:
                        Log.d("data", "handleMessage: Starting...");

                        adapter.clear();
                        adapter.notifyDataSetChanged();

                        progressBar.setProgress(0);
                        textViewDisplayAverage.setText("");
                        textViewProgressBar.setText("");

                        buttonAsync.setEnabled(false);
                        buttonThread.setEnabled(false);
                        seekBar.setEnabled(false);
                        break;

                    case DoWork.STATUS_PROGRESS:
                        Log.d("data", "handleMessage: " + msg.obj);
                        adapter.add(newNumber);
                        adapter.notifyDataSetChanged();

                        double sum = 0.0;
                        for (Double entry : numberList){
                            sum = sum + entry;
                        }

                        textViewProgressBar.setText(adapter.getCount() + "/" + String.valueOf(totalCount));

                        textViewDisplayAverage.setText(String.valueOf(sum / (double) adapter.getCount()));


                        break;

                    case DoWork.STATUS_STOP:
                        Log.d("data", "handleMessage: Stopping...");

                        buttonAsync.setEnabled(true);
                        buttonThread.setEnabled(true);
                        seekBar.setEnabled(true);
                        break;
                }

                progressBar.setProgress(adapter.getCount());
                return false;
            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewSeekProgress.setText(String.valueOf(progress) + " Times");
                progressBar.setMax(progress);
                Log.d("data", "onProgressChanged: " + progress);
                totalCount = progress;
                threadPool = Executors.newFixedThreadPool(totalCount);

                buttonAsync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);
                        textViewAverage.setVisibility(View.VISIBLE);
                        new MyAsyncTask().execute(progress);


                        buttonAsync.setEnabled(false);
                        buttonThread.setEnabled(false);
                        seekBar.setEnabled(false);

                        if (progress < 1){
                            Toast.makeText(MainActivity.this, "Enter a complexity greater than 1", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                buttonThread.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("data", "onClick: Thread Button Clicked");
                        progressBar.setVisibility(View.VISIBLE);
                        textViewAverage.setVisibility(View.VISIBLE);

                        //new Thread(new DoWork()).start();
                        threadPool.execute(new DoWork());

                        if (progress < 1){
                            Toast.makeText(MainActivity.this, "Enter a complexity greater than 1", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

//
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });






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
        protected void onPostExecute(ArrayList<Double> doubles) {
            super.onPostExecute(doubles);
            buttonAsync.setEnabled(true);
            buttonThread.setEnabled(true);
            seekBar.setEnabled(true);
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
    class DoWork implements  Runnable{
        static final int STATUS_START = 0x00;
        static final int STATUS_PROGRESS = 0x01;
        static final int STATUS_STOP = 0x02;

        @Override
        public void run() {
            Message startMessage = new Message();
            startMessage.what = STATUS_START;
            handler.sendMessage(startMessage);

            for (int i = 0; i < totalCount; i++){
                double number = HeavyWork.getNumber();

                Message message = new Message();
                message.what = STATUS_PROGRESS;
                message.obj = number;
                handler.sendMessage(message);
            }





            Message stopMessage = new Message();
            stopMessage.what = STATUS_STOP;
            handler.sendMessage(stopMessage);
        }
    }

}
