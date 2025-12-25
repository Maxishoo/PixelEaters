package com.example.dz4composekotlin.ui.contant_details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dz4composekotlin.R
import com.example.dz4composekotlin.data.model.Contact
import com.example.dz4composekotlin.data.model.Note
import com.example.dz4composekotlin.data.repository.ContactsRepository
import com.example.dz4composekotlin.data.repository.LoadState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    paddingValues: PaddingValues,
    contactId: Long,
    repository: ContactsRepository
) {
    val viewModel: ContactDetailsViewModel = viewModel(
        factory = ContactDetailsViewModelFactory(repository)
    )

    LaunchedEffect(contactId) {
        viewModel.loadContact(contactId)
        viewModel.loadNotes(contactId)
    }

    val contactState by viewModel.contactState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.tab_info),
        stringResource(R.string.tab_notes)
    )

    Scaffold(
        modifier = Modifier.padding(paddingValues)
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> ContactInfoTab(contactState)
                1 -> ContactNotesTab(viewModel, contactId)
            }
        }
    }
}

@Composable
fun ContactInfoTab(contactState: LoadState<Contact>) {
    when (contactState) {
        is LoadState.Loading ->
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())

        is LoadState.Error ->
            Text(stringResource(R.string.error_contact_load))

        is LoadState.Empty ->
            Text(stringResource(R.string.contact_not_found))

        is LoadState.Success -> {
            val contact = contactState.data
            var categoriesExpanded by remember { mutableStateOf(false) }
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("${stringResource(R.string.label_first_name)}: ${contact.firstName}")
                Text("${stringResource(R.string.label_last_name)}: ${contact.lastName}")
                contact.phone?.let {
                    Text("${stringResource(R.string.label_phone)}: $it")
                }
                contact.email?.let {
                    Text("${stringResource(R.string.label_email)}: $it")
                }
                contact.telegram?.let {
                    Text("${stringResource(R.string.label_telegram)}: $it")
                }
                contact.address?.let {
                    Text("${stringResource(R.string.label_address)}: $it")
                }
                contact.job?.let {
                    Text("${stringResource(R.string.label_job)}: $it")
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { categoriesExpanded = !categoriesExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.categories),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = if (categoriesExpanded)
                            Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.cd_expand_categories)
                    )
                }

                if (categoriesExpanded) {
                    if (contact.tags.isEmpty()) {
                        Text(stringResource(R.string.categories_empty))
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            contact.tags.forEach { tag ->
                                Text("#$tag")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactNotesTab(viewModel: ContactDetailsViewModel, contactId: Long) {
    val notesState by viewModel.notesState.collectAsState()
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    val notes = (notesState as? LoadState.Success<List<Note>>)?.data.orEmpty()
    val listState = rememberLazyListState()

    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index == listState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(isAtBottom) {
        if (isAtBottom && viewModel.canLoadMoreNotes()) {
            viewModel.loadNextNotesPage(contactId)
        }
    }

    when (notesState) {
        is LoadState.Loading ->
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

        is LoadState.Error ->
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.error_notes_load))
            }

        is LoadState.Empty ->
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(stringResource(R.string.notes_empty))
            }

        is LoadState.Success -> {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(notes) { _, note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedNote = note }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(note.text)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                note.createdAt,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                if (viewModel.canLoadMoreNotes()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            selectedNote?.let { note ->
                AlertDialog(
                    onDismissRequest = { selectedNote = null },
                    title = { Text(stringResource(R.string.note_title)) },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(note.text)
                            Text(
                                note.createdAt,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { selectedNote = null }) {
                            Text(stringResource(R.string.close))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { selectedNote = null }) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                )
            }
        }
    }
}
