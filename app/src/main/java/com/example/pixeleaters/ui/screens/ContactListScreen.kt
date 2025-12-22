package com.example.pixeleaters.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Контакты") },
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
                            Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                expanded = !uiState.paginationState.isLoadingMore,
                icon = {
                    Icon(Icons.Default.Add, contentDescription = "Добавить")
                },
                text = {
                    if (uiState.paginationState.isLoadingMore) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Добавить", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.searchContacts(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            CategoryFilterChips(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.filterByCategory(it) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (uiState.isRefreshing &&
                (uiState.searchQuery.isNotEmpty() || uiState.selectedCategory != null)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val contactsState = uiState.contactsState) {
                    is DataState.Loading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            repeat(5) {
                                ShimmerContactCard()
                            }
                        }
                    }

                    is DataState.Success -> {
                        if (contactsState.data.isEmpty()) {
                            EmptyStateView(
                                searchQuery = uiState.searchQuery,
                                selectedCategory = uiState.selectedCategory,
                                onClearFilters = {
                                    viewModel.searchContacts("")
                                    viewModel.filterByCategory(null)
                                }
                            )
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(contactsState.data) { contact ->
                                    ContactCard(
                                        contact = contact,
                                        onClick = {
                                            if (!uiState.paginationState.isLoadingMore) {
                                                onContactClick(contact.id)
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
                                                    CircularProgressIndicator(
                                                        strokeWidth = 2.dp
                                                    )
                                                    Text(
                                                        text = "Загрузка...",
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
                                                    IconButton(
                                                        onClick = { viewModel.loadMoreContacts() }
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Refresh,
                                                            contentDescription = "Повторить"
                                                        )
                                                    }
                                                }
                                            } else {
                                                Text(
                                                    text = "Потяните вверх для загрузки",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (!uiState.paginationState.hasMore &&
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
                                                text = "Все контакты загружены",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is DataState.Error -> {
                        ErrorStateView(
                            error = contactsState.message,
                            onRetry = { viewModel.loadInitialContacts() }
                        )
                    }
                }

                if (uiState.contactsState is DataState.Success &&
                    (uiState.contactsState as DataState.Success<List<Contact>>).data.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
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
        }
    }

    if (uiState.showAddDialog) {
        AddContactDialog(
            onDismiss = { viewModel.hideAddDialog() },
            onSave = { viewModel.addContact(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(query))
    }

    LaunchedEffect(query) {
        textFieldValue = TextFieldValue(query)
    }

    TextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onQueryChange(it.text)
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Поиск")
        },
        placeholder = { Text("Поиск контактов...") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier
    )
}

@Composable
fun CategoryFilterChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("Работа", "Друг", "Семья", "Учеба", "Коллега")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomFilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Все") }
        )

        categories.forEach { category ->
            CustomFilterChip(
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(
                        if (selectedCategory == category) null else category
                    )
                },
                label = { Text(category) }
            )
        }
    }
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
            contentDescription = "Ошибка",
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
            Text("Повторить попытку")
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
            contentDescription = "Нет контактов",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when {
                searchQuery.isNotEmpty() -> "Не найдено контактов по запросу \"$searchQuery\""
                selectedCategory != null -> "Нет контактов в категории \"$selectedCategory\""
                else -> "Контактов пока нет"
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        if (searchQuery.isNotEmpty() || selectedCategory != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onClearFilters) {
                Text("Сбросить фильтры")
            }
        }
    }
}