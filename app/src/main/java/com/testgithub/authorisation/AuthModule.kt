package com.testgithub.authorisation

import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


object AuthModule {

    fun create() = module {
        single { FirebaseAuth.getInstance() }

        single { AuthorizationUseCase(get(), get()) }

        viewModel { AuthorizationViewModel(get()) }
    }
}