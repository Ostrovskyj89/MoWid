package com.eleks.data.repository

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.mapper.mapToDomain
import com.eleks.data.mapper.toUserModel
import com.eleks.data.model.*
import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.model.QuoteModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivationPhraseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : MotivationPhraseRepository {

    override fun getCurrentUser() = firebaseDataSource.currentUser?.toUserModel()

    override fun getGroupsFlow(): Flow<List<GroupPhraseModel>> = combine(
        firebaseDataSource.userGroupsFlow,
        firebaseDataSource.groupsFlow,
        firebaseDataSource.selectedGroupsFlow
    ) { groups, userGroups, selectedGroups ->
        val allGroups = groups.merge(userGroups)
        if (selectedGroups.status == Status.ERROR) {
            throw selectedGroups.error ?: Exception("Unknown exception")
        }
        when (allGroups.status) {
            Status.SUCCESS -> allGroups.data?.distinctBy { it.id }
                ?.map { model -> model.mapToDomain(selectedGroups.data.orEmpty()) }
                ?: emptyList()
            Status.ERROR -> throw allGroups.error ?: Exception("Unknown exception")
        }
    }

    override fun getQuotes(groupId: String): Flow<List<QuoteModel>> {
        firebaseDataSource.subscribeAllGroupsQuotes(groupId)
        return combine(
            firebaseDataSource.quotesFlow,
            firebaseDataSource.userQuotesFlow,
            firebaseDataSource.selectedQuotesFlow
        ) { quotes, userQuotes, selectedQuotes ->
            val allQuotes = quotes.merge(userQuotes)
            if (selectedQuotes.status == Status.ERROR) {
                throw selectedQuotes.error ?: Exception("Unknown exception")
            }
            when (allQuotes.status) {
                Status.SUCCESS -> allQuotes.data?.map { model -> model.mapToDomain(selectedQuotes.data.orEmpty()) }
                    ?: emptyList()
                Status.ERROR -> throw allQuotes.error ?: Exception("Unknown exception")
            }
        }
    }

    override suspend fun addGroup(name: String, description: String) {
        firebaseDataSource.saveNewGroup(
            GroupDataModel(
                name = name,
                description = description
            )
        )
    }

    override suspend fun addQuote(groupId: String, quote: String, author: String) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        firebaseDataSource.saveNewQuote(
            groupId,
            QuoteDataModel(
                author = author,
                quote = quote,
                created = format.format(Date())
            )
        )
    }

    override suspend fun saveSelection(
        groupId: String,
        quoteId: String,
        shownAt: String,
        isSelected: Boolean
    ) {
        firebaseDataSource.saveSelection(
            groupId = groupId,
            quote = SelectedQuoteDataModel(
                quoteId,
                shownAt
            ),
            isSelected = isSelected
        )
    }
}

