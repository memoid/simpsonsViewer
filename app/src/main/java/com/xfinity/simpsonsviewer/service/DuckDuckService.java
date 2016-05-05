package com.xfinity.simpsonsviewer.service;

import com.xfinity.simpsonsviewer.entity.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gmoro on 5/4/2016.
 */
public interface DuckDuckService {

    @GET("?format=json")
    Call<Result> listCharacters(@Query("q")String q);

}
