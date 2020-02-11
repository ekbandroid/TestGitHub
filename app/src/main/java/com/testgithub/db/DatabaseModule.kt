package com.testgithub.db

import androidx.room.Room
import com.testgithub.InjectionModule
import org.koin.dsl.module
import java.util.concurrent.Executors

object DatabaseModule: InjectionModule {
    private const val DATABASE_NAME = "test_git_hub"

    override fun create() = module {
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
    }
}