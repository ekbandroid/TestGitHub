package com.testgithub.repositories.search.api

import com.testgithub.repositories.model.Repository
import io.reactivex.Single

class GitHubService(private val gitHubApi: GitHubApi) {

    fun searchRepositories(query: String, lastRequestedPage: Int, networkPageSize: Int): Single<List<Repository>> =
        gitHubApi.searchRepositories(
            query,
            lastRequestedPage,
            networkPageSize
        )
            .map { it.data }

}