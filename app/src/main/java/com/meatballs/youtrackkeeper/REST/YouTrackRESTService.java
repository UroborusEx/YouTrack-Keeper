package com.meatballs.youtrackkeeper.REST;

import com.google.gson.JsonArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
//YouTrack service Api
public interface YouTrackRESTService {

    @GET("/rest/admin/project")
    Call<List<ProjectModel>> GetProjects();
}
