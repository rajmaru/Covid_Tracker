package com.one.javacovidtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.one.javacovidtracker.api.ApiInterface;
import com.one.javacovidtracker.api.CountryData;
import com.one.javacovidtracker.api.RetrofitInstance;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    ApiInterface apiInterface;
    ArrayList<CountryData> list;
    TextView date, Confirmed, Active, Recovered, Deaths, Tests;
    PieChart pieChart;
    AlertDialog dialog;
    BroadcastReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_main);

        View view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null, false);
        dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();

        init();

        list = new ArrayList<>();

        apiInterface = RetrofitInstance.getRetrofitInstance().create(ApiInterface.class);

        apiInterface.getData().enqueue(new Callback<ArrayList<CountryData>>() {
            @Override
            public void onResponse(Call<ArrayList<CountryData>> call, Response<ArrayList<CountryData>> response) {
                list.addAll(response.body());
                pieChart.setVisibility(View.VISIBLE);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCountry().equals("India")) {
                            dialog.dismiss();
                            int confirm = Integer.parseInt(list.get(i).getCases());
                            int active = Integer.parseInt(list.get(i).getActive());
                            int recovered = Integer.parseInt(list.get(i).getRecovered());
                            int deaths = Integer.parseInt(list.get(i).getDeaths());
                            int tests = Integer.parseInt(list.get(i).getTests());

                            String sConfirm, sActive, sRecovered, sDeaths, sTests;

                            sConfirm = confirm+"";
                            Log.d("TAG", "Confirm = "+ sConfirm);
                            if(sConfirm.length() > 7){
                                sConfirm = sConfirm.substring(0,sConfirm.length()-7);
                                sConfirm = "+" + sConfirm + " cr";
                            }else if(sConfirm.length() > 5){
                                sConfirm = sConfirm.substring(0,sConfirm.length()-5);
                                sConfirm = "+" + sConfirm + " lac";
                            }

                            sActive = active+"";
                            Log.d("TAG", "Active = "+ sActive);
                            if(sActive.length() > 7){
                                sActive = sActive.substring(0,sActive.length()-7);
                                sActive = "+" + sActive + " cr";
                            }else if(sActive.length() > 5){
                                sActive = sActive.substring(0,sActive.length()-5);
                                sActive = "+" + sActive + " lac";
                            }

                            sRecovered = recovered+"";
                            Log.d("TAG", "Confirm = "+ sRecovered);
                            if(sRecovered.length() > 7){
                                sRecovered = sRecovered.substring(0,sRecovered.length()-7);
                                sRecovered = "+" + sRecovered + " cr";
                            }else if(sConfirm.length() > 5){
                                sRecovered = sRecovered.substring(0,sRecovered.length()-5);
                                sRecovered = "+" + sRecovered + " lac";
                            }

                            sDeaths = deaths+"";
                            Log.d("TAG", "Deaths = "+ sDeaths);
                            if(sDeaths.length() > 7){
                                sDeaths = sDeaths.substring(0,sDeaths.length()-7);
                                sDeaths = "+" + sDeaths + " cr";
                            }else if(sDeaths.length() > 5){
                                sDeaths = sDeaths.substring(0,sDeaths.length()-5);
                                sDeaths = "+" + sDeaths + " lac";
                            }

                            sTests = tests+"";
                            Log.d("TAG", "Tests = "+ sTests);
                            if(sTests.length() > 7){
                                sTests = sTests.substring(0,sTests.length()-7);
                                sTests = "+" + sTests + " cr";
                            }else if(sTests.length() > 5){
                                sTests = sTests.substring(0,sTests.length()-5);
                                sTests = "+" + sTests + " lac";
                            }

                            Confirmed.setText(sConfirm);
                            Active.setText(sActive);
                            Recovered.setText(sRecovered);
                            Deaths.setText(sDeaths);
                            Tests.setText(sTests);

                            setDate(list.get(i).getUpdated());

                            pieChart.addPieSlice(new PieModel("confirmed", confirm, getResources().getColor(R.color.yellow)));
                            pieChart.addPieSlice(new PieModel("active", active, getResources().getColor(R.color.blue)));
                            pieChart.addPieSlice(new PieModel("recovered", recovered, getResources().getColor(R.color.green)));
                            pieChart.addPieSlice(new PieModel("deaths", deaths, getResources().getColor(R.color.red)));
                            pieChart.startAnimation();
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "List is empty", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<CountryData>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    private void setDate(String updated){
        DateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        date.setText("Updated by " + dateformat.format(calendar.getTime()));
    }

    private void init() {
        myReceiver = new MyReceiver();
        Confirmed = findViewById(R.id.Confirmed);
        Active = findViewById(R.id.Active);
        Recovered = findViewById(R.id.Recovered);
        Deaths = findViewById(R.id.Deaths);
        Tests = findViewById(R.id.Tests);
        pieChart = findViewById(R.id.piechart);
        date = findViewById(R.id.date);
    }
}