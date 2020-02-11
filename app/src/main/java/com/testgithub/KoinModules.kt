package com.testgithub

import com.testgithub.authorization.AuthModule
import com.testgithub.db.DatabaseModule
import com.testgithub.repositories.favorites.FavoriteRepositoriesModule
import com.testgithub.repositories.search.SearchRepositoriesModule
import org.koin.core.module.Module

object KoinModules {
    fun create() = listOf(
        DatabaseModule.create(),
        SearchRepositoriesModule.create(),
        FavoriteRepositoriesModule.create(),
        AuthModule.create()
    )
}

interface InjectionModule {
    fun create(): Module
}