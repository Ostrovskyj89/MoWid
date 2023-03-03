package com.eleks.mowid.ui.feature.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.*
import androidx.glance.background
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.eleks.mowid.ui.feature.widget.composable.QuotesWidgetContent
import com.eleks.mowid.R

class QuotesWidget: GlanceAppWidget() {

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    @Composable
    override fun Content() {
        QuotesWidgetContent(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(androidx.glance.ImageProvider(R.drawable.widget_background))
                .appWidgetBackground()
                .padding(8.dp),
        )
    }

    companion object {
        const val QUOTE_PREFS_KEY = "QUOTE_PREFS_KEY"
        const val AUTHOR_PREFS_KEY = "AUTHOR_PREFS_KEY"
    }
}

class QuotesWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuotesWidget()
}
