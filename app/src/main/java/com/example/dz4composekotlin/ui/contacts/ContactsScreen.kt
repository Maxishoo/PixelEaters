package com.example.dz4composekotlin.ui.contacts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.dz4composekotlin.R
import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.repository.ContactsRepository
import com.example.dz4composekotlin.data.repository.LoadState

@Composable
fun ContactsScreen(
    paddingValues: PaddingValues,
    repository: ContactsRepository,
    onContactClick: (Long) -> Unit
) {
    val viewModel: ContactsViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel(
            factory = ContactsViewModelFactory(repository)
        )

    val state by viewModel.contactsState.collectAsState()

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        when (state) {
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is LoadState.Error -> {
                Text(
                    text = stringResource(R.string.error_contacts_load),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is LoadState.Empty -> {
                Text(
                    text = stringResource(R.string.contacts_empty),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is LoadState.Success -> {
                val contacts = (state as LoadState.Success<List<Contact>>).data

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(contacts) { index, contact ->
                        ContactItem(
                            contact = contact,
                            onClick = { onContactClick(contact.id) }
                        )

                        if (index == contacts.lastIndex) {
                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                    }

                    if (viewModel.canLoadMore()) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
    }
}
