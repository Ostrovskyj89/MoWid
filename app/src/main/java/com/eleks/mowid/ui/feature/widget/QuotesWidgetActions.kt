package com.eleks.mowid.ui.feature.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.eleks.mowid.di.ActionCallBackEntryPoint
import com.eleks.mowid.ui.worker.ExecutionOption
import dagger.hilt.android.EntryPointAccessors

class LeftArrowClickAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val workManager =
            EntryPointAccessors.fromApplication<ActionCallBackEntryPoint>(context).workManager()
        workManager.execute(ExecutionOption.PREVIOUS)
    }
}

class RightArrowClickAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val workManager =
            EntryPointAccessors.fromApplication<ActionCallBackEntryPoint>(context).workManager()
        workManager.execute(ExecutionOption.NEXT)
    }
}
