package com.eleks.mowid.ui.feature.widget

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.eleks.mowid.ui.feature.widget.QuotesWidget.Companion.AUTHOR_PREFS_KEY
import com.eleks.mowid.ui.feature.widget.QuotesWidget.Companion.QUOTE_PREFS_KEY

class LeftArrowClickAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
                    // TODO add mechanism to change quote
                    this[stringPreferencesKey(QUOTE_PREFS_KEY)] = ""
                    this[stringPreferencesKey(AUTHOR_PREFS_KEY)] = ""
                }
        }
        QuotesWidget().update(context, glanceId)
    }
}

class RightArrowClickAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) {
            it.toMutablePreferences()
                .apply {
                    // TODO add mechanism to change quote
                    this[stringPreferencesKey(QUOTE_PREFS_KEY)] = ""
                    this[stringPreferencesKey(AUTHOR_PREFS_KEY)] = ""
                }
        }
        QuotesWidget().update(context, glanceId)
    }
}
