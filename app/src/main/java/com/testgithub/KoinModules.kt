package com.testgithub

import com.testgithub.common.CommonModule
import com.testgithub.repositories.RepositoriesModule

object KoinModules {
    fun create() = listOf(
        CommonModule.create(),
        RepositoriesModule.createModule()
    )
}