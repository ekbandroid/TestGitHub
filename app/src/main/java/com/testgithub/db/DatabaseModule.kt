package com.testgithub.db

import androidx.room.Room
import org.koin.dsl.module
import java.util.concurrent.Executors

object DatabaseModule {
    private const val DATABASE_NAME = "test_git_hub"

    fun create() = module {
        single {
            Room.databaseBuilder(
                get(),
                TestGitHubDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .setQueryExecutor(Executors.newCachedThreadPool())
                .build()
        }
        single { get<TestGitHubDatabase>().favoriteRepositoriesDao() }
    }
}