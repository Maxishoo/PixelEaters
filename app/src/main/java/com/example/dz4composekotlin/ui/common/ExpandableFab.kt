package com.example.dz4composekotlin.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.ui.res.stringResource
import com.example.dz4composekotlin.R

@Composable
fun ExpandableFab(modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column {
                SmallFab(stringResource(R.string.fab_contact), Icons.Default.PersonAdd)
                Spacer(Modifier.height(8.dp))
                SmallFab(stringResource(R.string.fab_note), Icons.Default.Edit)
                Spacer(Modifier.height(8.dp))
                SmallFab(stringResource(R.string.fab_reminder), Icons.Default.Alarm)
                Spacer(Modifier.height(16.dp))
            }
        }

        FloatingActionButton(
            onClick = { expanded = !expanded }
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = if (expanded)
                    stringResource(R.string.fab_close_description)
                else
                    stringResource(R.string.fab_add_description)
            )
        }
    }
}

@Composable
private fun SmallFab(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    ExtendedFloatingActionButton(
        text = { Text(text) },
        icon = { Icon(icon, null) },
        onClick = { /* заглушка */ }
    )
}
