package com.testgithub

import com.testgithub.authorization.AuthorizationModule
import com.testgithub.db.DatabaseModule
import com.testgithub.repositories.favorite.FavoriteRepositoriesModule
import com.testgithub.repositories.search.SearchRepositoriesModule
import org.koin.core.module.Module

object KoinModules {
    fun create() = listOf(
        DatabaseModule.create(),
        SearchRepositoriesModule.create(),
        FavoriteRepositoriesModule.create(),
        AuthorizationModule.create()
    )
}

interface InjectionModule {
    fun create(): Module
}