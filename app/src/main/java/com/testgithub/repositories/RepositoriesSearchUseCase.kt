package com.testgithub.repositories

import com.testgithub.repositories.model.SearchRepositoriesResponse
import io.reactivex.Single

class RepositoriesSearchUseCase(private val gitHubApi: GitHubApi) {

    fun searchRepositories(text: String): Single<SearchRepositoriesResponse> =
        gitHubApi.searchRepositories(
            text, 1, 1000
        )
}