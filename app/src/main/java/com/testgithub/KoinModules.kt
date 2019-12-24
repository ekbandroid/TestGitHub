package com.testgithub

import com.testgithub.authorization.AuthModule
import com.testgithub.db.DatabaseModule
import com.testgithub.repositories.RepositoriesModule

object KoinModules {
    fun create() = listOf(
        DatabaseModule.create(),
        RepositoriesModule.createModule(),
        AuthModule.create()
    )
}