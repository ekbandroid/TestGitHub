package com.testgithub.repositories.search

import com.testgithub.InjectionModule
import com.testgithub.db.TestGitHubDatabase
import com.testgithub.repositories.search.api.GitHubApi
import com.testgithub.repositories.search.api.GitHubService
import com.testgithub.repositories.search.db.SearchedRepositoriesDatabaseGateway
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object SearchRepositoriesModule : InjectionModule {
    override fun create() = module {

        single { get<TestGitHubDatabase>().searchedRepositoriesDao() }

        single {
            SearchedRepositoriesDatabaseGateway(
                get()
            )
        }

        single {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return@single retrofit.create(GitHubApi::class.java)
        }

        single { GitHubService(get()) }

        single {
            SearchRepositoriesInteractor(
                get(),
                get(),
                get()
            )
        }

        viewModel {
            RepositoriesSearchViewModel(
                get(), get()
            )
        }
    }
}