package com.testgithub.repositories

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RepositoriesModule {
    fun createModule() = module {
        single {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return@single retrofit.create(GitHubApi::class.java)
        }
        single { RepositoriesSearchUseCase(get()) }
        viewModel { RepositoriesSearchViewModel(get()) }
    }
}