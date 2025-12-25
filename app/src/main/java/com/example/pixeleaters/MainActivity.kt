package com.example.pixeleaters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pixeleaters.ui.screens.ContactDetailScreen
import com.example.pixeleaters.ui.screens.ContactListScreen
import com.example.pixeleaters.ui.theme.ContactsAppTheme
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactsApp()
                }
            }
        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ContactsApp() {
    var selectedContactId by rememberSaveable { mutableStateOf<Long?>(null) }

    BackHandler(enabled = selectedContactId != null) {
        selectedContactId = null
    }

    AnimatedContent(
        targetState = selectedContactId,
        transitionSpec = {
            if (targetState != null) {
                (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it / 3 } + fadeOut())
            } else {
                (slideInHorizontally { it / 3 } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        },
        label = "contact_screen_transition"
    ) { contactId ->
        if (contactId != null) {
            ContactDetailScreen(
                contactId = contactId,
                onBackClick = { selectedContactId = null }
            )
        } else {
            ContactListScreen(
                onContactClick = { selectedContactId = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContactsAppPreview() {
    ContactsAppTheme {
        ContactsApp()
    }
}