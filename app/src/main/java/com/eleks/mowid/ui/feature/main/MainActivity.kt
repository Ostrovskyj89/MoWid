package com.eleks.mowid.ui.feature.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eleks.mowid.R
import com.eleks.mowid.base.ui.BaseActivity
import com.eleks.mowid.ui.navigation.AppNavigation
import com.eleks.mowid.ui.theme.MoWidTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<MainState, MainEvent, MainEffect, MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.viewModel.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoWidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(viewModel)
                }
            }
        }
        subscribeOnEvent()
    }

    override fun handleEffect(effect: MainEffect) {
        when (effect) {
            is MainEffect.ShowToast -> {
                Toast.makeText(this, getString(effect.messageId), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun subscribeOnEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.event.collect {
                    when (it) {
                        is MainEvent.SignIn -> createSignInIntent()
                        is MainEvent.SignOut -> signOut()
                        is MainEvent.NavigateToQuote -> {

                            val groupId = intent.getStringExtra(GROUP_ID)
                            val quoteId = intent.getStringExtra(QUOTE_ID)
//                            viewModel.navigateToQuote(groupId, quoteId)
                        }
                    }
                }
            }
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.LoginTheme)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                viewModel.signOutSuccess()
                Toast.makeText(this, getString(R.string.label_sign_out_success), Toast.LENGTH_LONG).show()
            }
    }

    companion object {

        private const val TAG = "MainActivity"
        private const val GROUP_ID = "groupId"
        private const val QUOTE_ID = "quoteId"

        fun start(context: Context, groupId: String, quoteId: String) {
            val intent = Intent(context, MainActivity::class.java)
                .apply {
                    putExtra(GROUP_ID, groupId)
                    putExtra(QUOTE_ID, quoteId)
                }
            context.startActivity(intent)
        }
    }
}
