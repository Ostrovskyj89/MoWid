package com.eleks.mowid.ui.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.SelectedQuoteDataModel
import com.eleks.data.model.Status
import com.eleks.data.preferences.LocalDataSource
import com.eleks.mowid.ui.feature.widget.QuotesWidgetReceiver
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.*

@HiltWorker
class QuotesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workParams: WorkerParameters,
    private val firebaseDataSource: FirebaseDataSource,
    private val localDataSource: LocalDataSource
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val option = ExecutionOption.valueOf(localDataSource.quoteChangeOption ?: ExecutionOption.REGULAR.name)
        Log.d("QuotesWorker", "doWork option = ${option.name}")
        val result = firebaseDataSource.getSelectedQuotes()
        return when (result.status) {
            Status.SUCCESS -> {
                showNextQuote(result.data, option)
                Result.success()
            }
            Status.ERROR -> Result.failure()
        }
    }

    private suspend fun showNextQuote(quotes: List<SelectedQuoteDataModel>?, option: ExecutionOption) {
        quotes?.let {
            when (option) {
                ExecutionOption.REGULAR -> showRegularQuote(it)
                ExecutionOption.NEXT -> showNextQuote(it)
                ExecutionOption.PREVIOUS -> showPreviousQuote(it)
            }
        }
    }

    private suspend fun showRegularQuote(quotes: List<SelectedQuoteDataModel>) {
        quotes.sortedBy { it.shownAt }.firstOrNull()?.let {
            QuotesWidgetReceiver.updateWidget(
                quote = it.quote,
                author = it.author,
                memeUrl = it.memeUrl,
                context = context
            )
            updateShownQuote(it)
        }
    }

    private suspend fun showNextQuote(quotes: List<SelectedQuoteDataModel>) {
        val currentQuote = quotes.sortedBy { it.shownAt }.lastOrNull()
        val currentQuoteIndex = quotes.indexOf(currentQuote)
        val nextQuote = when {
            currentQuoteIndex == quotes.lastIndex -> quotes.first()
            currentQuoteIndex >= 0 -> quotes[currentQuoteIndex + 1]
            else -> null
        }
        nextQuote?.let {
            QuotesWidgetReceiver.updateWidget(
                quote = it.quote,
                author = it.author,
                memeUrl = it.memeUrl,
                context = context
            )
            updateShownQuote(it)
        }
        localDataSource.quoteChangeOption = ExecutionOption.REGULAR.name
    }

    private suspend fun showPreviousQuote(quotes: List<SelectedQuoteDataModel>) {
        val currentQuote = quotes.sortedBy { it.shownAt }.lastOrNull()
        val currentQuoteIndex = quotes.indexOf(currentQuote)
        val previousQuote = when {
            currentQuoteIndex == 0 -> quotes.last()
            currentQuoteIndex > 0 -> quotes[currentQuoteIndex - 1]
            else -> null
        }
        previousQuote?.let {
            QuotesWidgetReceiver.updateWidget(
                quote = it.quote,
                author = it.author,
                memeUrl = it.memeUrl,
                context = context
            )
            updateShownQuote(it)
        }
        localDataSource.quoteChangeOption = ExecutionOption.REGULAR.name
    }

    private suspend fun updateShownQuote(quote: SelectedQuoteDataModel) {
        firebaseDataSource.updateSelectedQuote(
            groupId = quote.groupId ?: "",
            quoteId = quote.id ?: "",
            shownTime = Date().time
        )
    }

    companion object {
        const val TAG = "quotes-worker"
    }
}
