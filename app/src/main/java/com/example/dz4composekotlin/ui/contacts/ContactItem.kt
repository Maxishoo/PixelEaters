package com.example.dz4composekotlin.ui.contacts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.dz4composekotlin.data.model.Contact
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.dz4composekotlin.R


@Composable
fun ContactItem(
    contact: Contact,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(colorResource(R.color.contact_color))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {

        // Аватар
        if (contact.avatarUrl != null) {
            Image(
                painter = rememberAsyncImagePainter(contact.avatarUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        } else {
            // Иконка профиля
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.purple_200)),
                contentAlignment = Alignment.Center
            ) {
                Text(contact.firstName.firstOrNull()?.toString() ?: "?")
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = contact.fullName, style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                contact.tags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}