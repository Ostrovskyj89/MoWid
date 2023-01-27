package com.eleks.mowid.feature.home

import androidx.fragment.app.viewModels
import com.eleks.mowid.base.ui.BaseFragment
import com.eleks.mowid.feature.main.MainState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeState, HomeEvent, HomeEffect, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()

    override fun handleEffect(effect: HomeEffect) {
        //TODO:
    }

}