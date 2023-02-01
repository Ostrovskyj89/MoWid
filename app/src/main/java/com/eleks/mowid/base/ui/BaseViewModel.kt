package com.eleks.mowid.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val EFFECTS_KEY = "effects_key"

abstract class BaseViewModel<
    State : UiState,
    Event : UiEvent,
    Effect : UiEffect> : ViewModel() {

    protected abstract fun handleEvent(event: Event)

    abstract fun createInitialState(): State

    private val initialState: State by lazy { createInitialState() }

    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event = _event.asSharedFlow()

    private val _effect: Channel<Effect> = Channel()
    val effect = _effect.receiveAsFlow()

    init {
        subscribeOnEvent()
    }

    fun setEvent(event: Event) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    protected fun setState(proceed: State.() -> State) {
        val newState = _uiState.value.proceed()
        _uiState.value = newState
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    private fun subscribeOnEvent() {
        viewModelScope.launch {
            event.collect { handleEvent(it) }
        }
    }
}