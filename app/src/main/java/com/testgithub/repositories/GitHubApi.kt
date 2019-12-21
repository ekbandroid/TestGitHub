package com.testgithub.repositories

import com.testgithub.repositories.model.SearchRepositoriesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") searchText: String,
        @Query("page") page: Int,
        @Query("per_page") countPerPage: Int
    ): Single<SearchRepositoriesResponse>
}
