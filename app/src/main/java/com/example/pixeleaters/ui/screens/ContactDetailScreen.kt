package com.example.pixeleaters.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pixeleaters.data.model.DataState
import com.example.pixeleaters.ui.components.CategoryChip
import com.example.pixeleaters.viewmodel.ContactViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailScreen(
    contactId: Long,
    onBackClick: () -> Unit,
    viewModel: ContactViewModel = viewModel(factory = ContactViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(contactId) {
        viewModel.selectContact(contactId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали контакта") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    uiState.selectedContactState.let { state ->
                        if (state is DataState.Success && state.data != null) {
                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(state.data.id, !state.data.isFavorite)
                                }
                            ) {
                                Icon(
                                    imageVector = if (state.data.isFavorite) Icons.Default.Favorite
                                    else Icons.Default.FavoriteBorder,
                                    contentDescription = if (state.data.isFavorite) "Убрать из избранного"
                                    else "Добавить в избранное",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    state.data.let {
                                        viewModel.deleteContact(it)
                                        onBackClick()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.White)
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState.selectedContactState) {
                is DataState.Loading -> {
                    CircularProgressIndicator()
                }

                is DataState.Success -> {
                    val contact = state.data
                    if (contact != null) {
                        ContactDetailContent(
                            contact = contact,
                            onBackClick = onBackClick,
                            viewModel = viewModel,
                            context = context
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Контакт не найден")
                            Button(onClick = onBackClick) {
                                Text("Вернуться")
                            }
                        }
                    }
                }

                is DataState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = onBackClick) {
                            Text("Вернуться")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactDetailContent(
    contact: com.example.pixeleaters.data.model.Contact,
    onBackClick: () -> Unit,
    viewModel: ContactViewModel,
    context: android.content.Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${contact.firstName} ${contact.lastName}",
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 24.dp)
        )

        if (contact.isFavorite) {
            Row(
                modifier = Modifier.padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Избранное",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "В избранном",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (contact.categories.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                contact.categories.forEach { category ->
                    CategoryChip(
                        category = category,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                contact.phoneNumber?.let { phone ->
                    ContactInfoRow(
                        icon = Icons.Default.Call,
                        title = "Телефон",
                        value = phone,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:$phone".toUri()
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                contact.email?.let { email ->
                    ContactInfoRow(
                        icon = Icons.Default.Email,
                        title = "Email",
                        value = email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:$email".toUri()
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                contact.telegram?.let { telegram ->
                    ContactInfoRow(
                        icon = Icons.Default.Message,
                        title = "Telegram",
                        value = telegram,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://t.me/$telegram".toUri()
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                contact.address?.let { address ->
                    ContactInfoRow(
                        icon = Icons.Default.LocationOn,
                        title = "Адрес",
                        value = address,
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "geo:0,0?q=${Uri.encode(address)}".toUri()
                            )
                            context.startActivity(intent)
                        }
                    )
                }

                contact.workplace?.let { workplace ->
                    ContactInfoRow(
                        icon = Icons.Default.Work,
                        title = "Работа",
                        value = workplace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    contact.phoneNumber?.let { phone ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = "tel:$phone".toUri()
                        }
                        context.startActivity(intent)
                    }
                },
                enabled = contact.phoneNumber != null
            ) {
                Icon(Icons.Default.Call, contentDescription = "Позвонить", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Позвонить")
            }

            Button(
                onClick = {
                    when {
                        contact.phoneNumber != null -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "smsto:${contact.phoneNumber}".toUri()
                            }
                            context.startActivity(intent)
                        }
                        contact.email != null -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:${contact.email}".toUri()
                            }
                            context.startActivity(intent)
                        }
                        contact.telegram != null -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = "https://t.me/${contact.telegram}".toUri()
                            }
                            context.startActivity(intent)
                        }
                    }
                },
                enabled = contact.phoneNumber != null || contact.email != null || contact.telegram != null
            ) {
                Icon(Icons.Default.Message, contentDescription = "Написать", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Написать")
            }
        }
    }
}

@Composable
private fun ContactInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    title: String,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(24.dp)
            )
        } ?: Spacer(modifier = Modifier.width(32.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 16.sp
            )
        }
    }
}