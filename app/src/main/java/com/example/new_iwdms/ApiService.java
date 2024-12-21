package com.example.new_iwdms;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("Tank/GetCityDetails")
    Call<List<CityDetails>> getCityDetails();
}
