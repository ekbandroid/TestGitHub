package com.testgithub.repositories.favorites

import com.testgithub.InjectionModule
import com.testgithub.db.TestGitHubDatabase
import com.testgithub.repositories.favorites.db.FavoriteRepositoriesDatabaseGateway
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object FavoriteRepositoriesModule: InjectionModule {
    override fun create() = module {
        single { get<TestGitHubDatabase>().favoriteRepositoriesDao() }

        single {
            FavoriteRepositoriesDatabaseGateway(
                get()
            )
        }

        single {
            FavoriteRepositoriesInteractor(
                get(),
                get()
            )
        }

        viewModel {
            FavoriteRepositoriesViewModel(
                get()
            )
        }
    }
}