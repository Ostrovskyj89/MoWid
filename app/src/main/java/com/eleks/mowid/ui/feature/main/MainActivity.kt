package com.eleks.mowid.ui.feature.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.eleks.mowid.base.ui.BaseActivity
import com.eleks.mowid.ui.navigation.AppNavigation
import com.eleks.mowid.ui.theme.MoWidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainState, MainEvent, MainEffect, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()

    override fun handleEffect(effect: MainEffect) {
        //TODO:
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoWidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
