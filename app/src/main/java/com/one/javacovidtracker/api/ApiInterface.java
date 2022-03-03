package com.one.javacovidtracker.api;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("countries")
    Call<ArrayList<CountryData>> getData();
}
