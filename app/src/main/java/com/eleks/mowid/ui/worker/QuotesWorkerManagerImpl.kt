package com.eleks.mowid.ui.worker

import androidx.work.*
import com.eleks.data.preferences.LocalDataSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class QuotesWorkerManagerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val localDataSource: LocalDataSource
) : QuotesWorkerManager {

    override fun execute(option: ExecutionOption) = enqueueWorker(option)

    private fun enqueueWorker(option: ExecutionOption) {
        workManager.enqueueUniquePeriodicWork(
            QuotesWorker.TAG,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            buildRequest(option)
        )
    }

    private fun buildRequest(option: ExecutionOption): PeriodicWorkRequest {
        saveOption(option)
        // TODO change repeatInterval value when settings screen will be ready
        return PeriodicWorkRequestBuilder<QuotesWorker>(20, TimeUnit.MINUTES)
            .addTag(QuotesWorker.TAG)
            .setConstraints(getDRMConstraints())
            .build()
    }

    private fun saveOption(option: ExecutionOption) {
        localDataSource.quoteChangeOption = option.name
    }

    companion object {
        private fun getDRMConstraints(): Constraints {
            return Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        }
    }
}
