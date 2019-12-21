package com.testgithub

import com.testgithub.repositories.RepositoriesModule

object KoinModules {
    fun create() = listOf(
        RepositoriesModule.createModule()
    )
}