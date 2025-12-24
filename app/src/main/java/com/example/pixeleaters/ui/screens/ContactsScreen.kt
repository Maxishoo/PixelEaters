package com.example.pixeleaters.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pixeleaters.R
import com.example.pixeleaters.data.model.Contact
import com.example.pixeleaters.data.model.UiState
import com.example.pixeleaters.ui.components.AppTopBar
import com.example.pixeleaters.ui.components.TagChip
import com.example.pixeleaters.ui.theme.ContactCardBackground
import com.example.pixeleaters.ui.theme.ContactCardStripe
import com.example.pixeleaters.ui.theme.ContactsListBackground
import com.example.pixeleaters.ui.theme.IconDark
import com.example.pixeleaters.ui.theme.PrimaryBlue
import com.example.pixeleaters.ui.theme.TagChipText
import com.example.pixeleaters.ui.theme.TagGreenBackground
import com.example.pixeleaters.ui.theme.TagRedBackground
import com.example.pixeleaters.ui.theme.TagTealBackground
import com.example.pixeleaters.ui.theme.TextPrimary
import com.example.pixeleaters.viewmodel.ContactViewModel
import com.valentinilk.shimmer.shimmer

@Composable
fun ContactsScreen(
    onNavigateToReminders: () -> Unit,
    viewModel: ContactViewModel = viewModel()
) {
    val contactsState by viewModel.contactsState.collectAsState()

    Scaffold(
        containerColor = ContactsListBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* nothing */ },
                containerColor = PrimaryBlue.copy(alpha=0.9f),
                contentColor = Color.White,
                shape = RoundedCornerShape(10.dp),
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .size(79.dp)
                    .border(
                        width=1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.fab_add_description),
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ContactsListBackground)
        ) {
            // Top App Bar
            ContactsTopBar(onNavigateToReminders = onNavigateToReminders)

            // Content
            when (val state = contactsState) {
                is UiState.Loading -> {
                    ContactsLoadingState()
                }
                is UiState.Success -> {
                    ContactsList(contacts = state.data)
                }
                is UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.retry() }
                    )
                }
                is UiState.Empty -> {
                    EmptyState(message = stringResource(R.string.empty_contacts))
                }
            }
        }
    }
}

@Composable
private fun ContactsTopBar(onNavigateToReminders: () -> Unit) {
    AppTopBar(backgroundColor = Color.White, showDivider = true) {

        IconButton(onClick = {  }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.back_button_description),
                tint = IconDark,
                modifier = Modifier.size(41.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Right icons row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            IconButton(onClick = {  }) {
                Icon(
                    painter = painterResource(R.drawable.button_search),
                    contentDescription = stringResource(R.string.search_button_description),
                    tint = IconDark
                )
            }

            IconButton(onClick = onNavigateToReminders) {
                Icon(
                    painter = painterResource(R.drawable.button_todo),
                    contentDescription = stringResource(R.string.notifications_button_description),
                    tint = IconDark
                )
            }

            IconButton(onClick = { /* Profile action */ }) {
                Icon(
                    painter = painterResource(R.drawable.button_user),
                    contentDescription = stringResource(R.string.notifications_button_description),
                    tint = IconDark
                )
            }
        }
    }
}

@Composable
private fun ContactsList(contacts: List<Contact>) {
    val listState = rememberLazyListState()
    
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(ContactsListBackground),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }
        itemsIndexed(
            items = contacts,
            key = { _, contact -> contact.id }
        ) { index, contact ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                        slideInVertically(
                            animationSpec = tween(300, delayMillis = index * 50),
                            initialOffsetY = { it / 2 }
                        )
            ) {
                ContactItem(
                    contact = contact,
                    rowIndex = index
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContactItem(
    contact: Contact,
    rowIndex: Int
) {
    val shape = RoundedCornerShape(10.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = ContactCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Left marker
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(15.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                    .background(ContactCardStripe)
                    .align(Alignment.CenterStart)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 11.dp, top = 6.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = contact.name,
                        fontSize = 18.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        contact.tags.forEach { tag ->
                            ContactTag(tag = tag)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image(
                    painter = painterResource(R.drawable.generic_avatar),
                    contentDescription = stringResource(R.string.contact_avatar_description),
                    modifier = Modifier.size(60.dp) // +15% from 58dp
                )
            }
        }
    }
}


@Composable
private fun ContactTag(tag: String) {
    val normalized = tag.trim().lowercase()
    val bg = when (normalized) {
        "коллега" -> TagRedBackground
        "работа" -> TagTealBackground
        else -> TagGreenBackground
    }
    TagChip(
        text = stringResource(R.string.tag_format, tag),
        backgroundColor = bg,
        textColor = TagChipText
    )
}

@Composable
private fun ContactsLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .shimmer()
    ) {
        repeat(8) { index ->
            ShimmerContactItem(rowIndex = index)
        }
    }
}

@Composable
private fun ShimmerContactItem(rowIndex: Int) {
    val shape = RoundedCornerShape(18.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = ContactCardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(13.2.dp) // keep same as real item
                    .clip(RoundedCornerShape(topStart = 18.dp, bottomStart = 18.dp))
                    .background(ContactCardStripe.copy(alpha = 0.35f))
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 19.2.dp, end = 11.dp, top = 11.dp, bottom = 11.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(3) {
                        Box(
                            modifier = Modifier
                                .width(44.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.error_occurred),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = stringResource(R.string.retry))
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.empty_state_description),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}
