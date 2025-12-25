package com.example.pixeleaters.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pixeleaters.R
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.DataState
import com.example.pixeleaters.ui.components.AddContactDialog
import com.example.pixeleaters.ui.components.ContactCard
import com.example.pixeleaters.ui.components.CustomFilterChip
import com.example.pixeleaters.ui.components.ShimmerContactCard
import com.example.pixeleaters.viewmodel.ContactViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onContactClick: (Long) -> Unit,
    viewModel: ContactViewModel = viewModel(factory = ContactViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val isNearBottom by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount

            lastVisibleItem?.index?.let { lastIndex ->
                lastIndex >= totalItems - 5
            } ?: false
        }
    }

    LaunchedEffect(isNearBottom) {
        if (isNearBottom &&
            uiState.paginationState.hasMore &&
            !uiState.paginationState.isLoadingMore &&
            uiState.searchQuery.isEmpty() &&
            uiState.selectedCategory == null) {
            viewModel.loadMoreContacts()
        }
    }
    var searchText by remember { mutableStateOf(uiState.searchQuery) }

    LaunchedEffect(uiState.searchQuery) {
        searchText = uiState.searchQuery
    }

    LaunchedEffect(searchText) {
        kotlinx.coroutines.delay(300)
        if (searchText != uiState.searchQuery) {
            viewModel.searchContacts(searchText)
        }
    }
    LaunchedEffect(uiState.searchQuery, uiState.selectedCategory) {
        listState.scrollToItem(0)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 225.dp,
                bottom = 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            when (val contactsState = uiState.contactsState) {
                is DataState.Loading -> {
                    items(20) {
                        ShimmerContactCard()
                    }
                }

                is DataState.Success -> {
                    if (contactsState.data.isEmpty()) {
                        item {
                            EmptyStateView(
                                searchQuery = uiState.searchQuery,
                                selectedCategory = uiState.selectedCategory,
                                onClearFilters = {
                                    searchText = ""
                                    viewModel.searchContacts("")
                                    viewModel.filterByCategory(null)
                                }
                            )
                        }
                    } else {
                        items(
                            items = contactsState.data,
                            key = { contact -> contact.id }
                        ) { contact ->
                            val contactId = contact.id
                            ContactCard(
                                contact = contact,
                                onClick = {
                                    if (!uiState.paginationState.isLoadingMore) {
                                        onContactClick(contactId)
                                    }
                                },
                                onToggleFavorite = {
                                    viewModel.toggleFavorite(contact.id, !contact.isFavorite)
                                }
                            )
                        }

                        if (uiState.paginationState.hasMore &&
                            uiState.searchQuery.isEmpty() &&
                            uiState.selectedCategory == null) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (uiState.paginationState.isLoadingMore) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            CircularProgressIndicator(strokeWidth = 2.dp)
                                            Text(
                                                text = stringResource(R.string.try_loading),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    } else if (uiState.paginationState.errorLoadingMore != null) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = uiState.paginationState.errorLoadingMore!!,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            IconButton(onClick = { viewModel.loadMoreContacts() }) {
                                                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.error_retry))
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (!uiState.paginationState.hasMore &&
                            contactsState.data.isNotEmpty() &&
                            uiState.searchQuery.isEmpty() &&
                            uiState.selectedCategory == null) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.contacts_loaded),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }

                is DataState.Error -> {
                    item {
                        ErrorStateView(
                            error = contactsState.message,
                            onRetry = { viewModel.loadInitialContacts() }
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                )
                .zIndex(1f)
        ) {
            TopAppBar(
                title = { Text(stringResource(R.string.contacts_label)) },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshContacts() },
                        enabled = !uiState.isRefreshing
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.update_description))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                modifier = Modifier.background(Color.Transparent)
            )
            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp)
            )
            CategoryFilterChips(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.filterByCategory(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(36.dp)
                .zIndex(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(colorResource(R.color.add_button).copy(alpha = 0.95f))
                .clickable { viewModel.showAddDialog() }
                .padding(16.dp)
        ) {
            if (uiState.paginationState.isLoadingMore) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_description),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        if (uiState.contactsState is DataState.Success &&
            (uiState.contactsState as DataState.Success<List<Contact>>).data.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 24.dp)
                    .zIndex(1f)
            ) {
                Text(
                    text = "${(uiState.contactsState as DataState.Success<List<Contact>>).data.size} из ${uiState.paginationState.totalItems}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }

    if (uiState.showAddDialog) {
        AddContactDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onSave = { viewModel.addContact(it) }
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_content_description)
            )
        },
        placeholder = {
            Text(stringResource(R.string.search_placeholder))
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    )
}

@Composable
fun CategoryFilterChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryStrings = stringArrayResource(R.array.categories)
    val categories = listOf(stringResource(R.string.category_all)) + categoryStrings.toList()
    val categoryAll = stringResource(R.string.category_all)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy((-12).dp),
        content = {
            for (category in categories) {
                val isSelected = selectedCategory == category
                CustomFilterChip(
                    selected = isSelected,
                    onClick = {
                        onCategorySelected(
                            if (isSelected || category == categoryAll) null else category
                        )
                    },
                    label = { Text(category) }
                )
            }
        }
    )
}

@Composable
fun ErrorStateView(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = stringResource(R.string.error_state_content_description),
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(stringResource(R.string.error_state_retry))
        }
    }
}

@Composable
fun EmptyStateView(
    searchQuery: String,
    selectedCategory: String?,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = stringResource(R.string.empty_state_no_contacts),
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        val message = when {
            searchQuery.isNotEmpty() -> {
                stringResource(R.string.empty_state_no_contacts_by_search, searchQuery)
            }
            selectedCategory != null -> {
                stringResource(R.string.empty_state_no_contacts_in_category, selectedCategory)
            }
            else -> {
                stringResource(R.string.empty_state_no_contacts_yet)
            }
        }

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        if (searchQuery.isNotEmpty() || selectedCategory != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClearFilters) {
                Text(stringResource(R.string.empty_state_clear_filters))
            }
        }
    }
}