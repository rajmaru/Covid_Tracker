package com.one.javacovidtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
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
    ProgressDialog progressBar;
    TextView date, Confirmed, Active, Recovered, Deaths, Tests, Today_Confirmed, Today_Active, Today_Recovered, Today_Deaths, Today_Tests;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        progressBar.setMessage("Loading ...");
        progressBar.show();

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
                            progressBar.dismiss();
                            int confirm = Integer.parseInt(list.get(i).getCases());
                            int active = Integer.parseInt(list.get(i).getActive());
                            int recovered = Integer.parseInt(list.get(i).getRecovered());
                            int deaths = Integer.parseInt(list.get(i).getDeaths());
                            int tests = Integer.parseInt(list.get(i).getTests());

                            Confirmed.setText(NumberFormat.getInstance().format(confirm));
                            Active.setText(NumberFormat.getInstance().format(active));
                            Recovered.setText(NumberFormat.getInstance().format(recovered));
                            Deaths.setText(NumberFormat.getInstance().format(deaths));
                            Tests.setText(NumberFormat.getInstance().format(tests));

                            setDate(list.get(i).getUpdated());

                            Today_Confirmed.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayCases())));
                            Today_Recovered.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayRecovered())));
                            Today_Deaths.setText(NumberFormat.getInstance().format(Integer.parseInt(list.get(i).getTodayDeaths())));

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

    private void setDate(String updated){
        DateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        date.setText("Updated by " + dateformat.format(calendar.getTime()));
    }

    private void init() {
        Confirmed = findViewById(R.id.Confirmed);
        Active = findViewById(R.id.Active);
        Recovered = findViewById(R.id.Recovered);
        Deaths = findViewById(R.id.Deaths);
        Tests = findViewById(R.id.Tests);
        Today_Confirmed = findViewById(R.id.Today_Confirmed);
        Today_Active = findViewById(R.id.Today_Active);
        Today_Recovered = findViewById(R.id.Today_Recovered);
        Today_Deaths = findViewById(R.id.Today_Deaths);
        Today_Tests = findViewById(R.id.Today_Tests);
        pieChart = findViewById(R.id.piechart);
        date = findViewById(R.id.date);
    }
}