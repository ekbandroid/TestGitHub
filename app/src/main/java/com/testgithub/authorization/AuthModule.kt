package com.testgithub.authorization

import com.google.firebase.auth.FirebaseAuth
import com.testgithub.InjectionModule
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AuthModule: InjectionModule {

    override fun create() = module {
        single { FirebaseAuth.getInstance() }

        single { AuthorizationInteractor(get(), get()) }

        viewModel { AuthorizationViewModel(get()) }
    }
}