package com.testgithub.repositories

import com.testgithub.repositories.model.Repository
import io.reactivex.Single
import timber.log.Timber

class RepositoriesSearchUseCase(private val gitHubApi: GitHubApi) {

    fun searchRepositories(text: String, page: Int, countPerPage: Int): Single<List<Repository>> {
        Timber.d("searchRepositories request $text $page")
        return gitHubApi.searchRepositories(text, page, countPerPage)
            .map { it.data }
    }

}