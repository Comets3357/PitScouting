package com.example.pitscoutingapp2023.dao;

import com.example.pitscoutingapp2023.common.PostDataResponse;
import com.example.pitscoutingapp2023.common.TeamPitScout;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ScoutingDataServerAPIService {

    @POST("/app/uploadPitData")
    Call<PostDataResponse> sendDataToServer(@Body List<TeamPitScout> dataToSend);

}

