package com.testgithub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.testgithub.extention.replaceFragment
import com.testgithub.repositories.RepositoriesSearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(RepositoriesSearchFragment())
        repositoriesSeachFrameLayout
    }
}
