package com.eleks.mowid.ui.feature.bottomsheet

import androidx.annotation.StringRes
import com.eleks.mowid.R

sealed class BottomSheetUIState(
    @StringRes
    val header: Int,
    @StringRes
    val hint1: Int,
    @StringRes
    val hint2: Int,
    @StringRes
    val buttonLabel: Int,
    open val id: String? = null,
    open val text1: String = "",
    open val text2: String = "",
) {
    object AddGroupBottomSheet : BottomSheetUIState(
        header = R.string.title_add_group,
        hint1 = R.string.label_group,
        hint2 = R.string.label_description,
        buttonLabel = R.string.label_add,
    )

    object AddQuoteBottomSheet : BottomSheetUIState(
        header = R.string.title_add_quote,
        hint1 = R.string.label_quote,
        hint2 = R.string.label_author,
        buttonLabel = R.string.label_add,
    )

    data class EditGroupBottomSheet(
        override val id: String,
        override val text1: String,
        override val text2: String
    ) : BottomSheetUIState(
            header = R.string.title_edit_group,
            hint1 = R.string.label_group,
            hint2 = R.string.label_description,
            buttonLabel = R.string.label_edit,
            id = id,
            text1 = text1,
            text2 = text2,
        )


    data class EditQuoteBottomSheet(
        override val id: String,
        override val text1: String,
        override val text2: String
    ) : BottomSheetUIState(
            header = R.string.title_edit_quote,
            hint1 = R.string.label_quote,
            hint2 = R.string.label_author,
            buttonLabel = R.string.label_edit,
            id = id,
            text1 = text1,
            text2 = text2,
        )
}
