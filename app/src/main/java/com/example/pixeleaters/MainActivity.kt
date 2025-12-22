package com.example.pixeleaters

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

//@AndroidEntryPoint
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

//annotation class AndroidEntryPoint

@Composable
fun ContactsApp() {
    var selectedContactId by rememberSaveable { mutableStateOf<Long?>(null) }

    when {
        selectedContactId != null -> {
            ContactDetailScreen(
                contactId = selectedContactId!!,
                onBackClick = { selectedContactId = null }
            )
        }
        else -> {
            ContactListScreen(
                onContactClick = { contactId -> selectedContactId = contactId }
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