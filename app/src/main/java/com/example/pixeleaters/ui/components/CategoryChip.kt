package com.example.pixeleaters.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixeleaters.R
import androidx.compose.ui.res.colorResource

enum class ContactCategory(
    @StringRes val stringRes: Int
) {
    WORK(R.string.category_work),
    STUDY(R.string.category_study),
    FRIEND(R.string.category_friend),
    FAMILY(R.string.category_family),
    COLLEAGUE(R.string.category_colleague);
}

@Composable
fun CategoryChip(
    categoryLabel: String,
    modifier: Modifier = Modifier
) {
    val category = ContactCategory.entries.find { stringResource(it.stringRes) == categoryLabel }

    val backgroundColor = when (category) {
        ContactCategory.WORK -> colorResource(R.color.category_work)
        ContactCategory.STUDY -> colorResource(R.color.category_study)
        ContactCategory.FRIEND -> colorResource(R.color.category_friend)
        ContactCategory.FAMILY -> colorResource(R.color.category_family)
        ContactCategory.COLLEAGUE -> colorResource(R.color.category_colleague)
        null -> colorResource(R.color.category_default)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColorFor(backgroundColor)
    ) {
        Text(
            text = category?.let { stringResource(it.stringRes) } ?: categoryLabel,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
    }
}