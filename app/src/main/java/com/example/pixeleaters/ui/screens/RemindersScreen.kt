package com.example.pixeleaters.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pixeleaters.R
import com.example.pixeleaters.data.model.Reminder
import com.example.pixeleaters.data.model.UiState
import com.example.pixeleaters.ui.components.AppTopBar
import com.example.pixeleaters.ui.components.AppTopBarDefaults
import com.example.pixeleaters.ui.components.TagChip
import com.example.pixeleaters.ui.theme.ButtonBlue
import com.example.pixeleaters.ui.theme.CardWhite
import com.example.pixeleaters.ui.theme.IconDark
import com.example.pixeleaters.ui.theme.IconGray
import com.example.pixeleaters.ui.theme.SurfaceGray
import com.example.pixeleaters.ui.theme.TagChipText
import com.example.pixeleaters.ui.theme.TagRedBackground
import com.example.pixeleaters.ui.theme.TextHint
import com.example.pixeleaters.ui.theme.TextPrimary
import com.example.pixeleaters.ui.theme.TextSecondary
import com.example.pixeleaters.viewmodel.ReminderViewModel
import com.valentinilk.shimmer.shimmer

@Composable
fun RemindersScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReminderViewModel = viewModel()
) {
    val remindersState by viewModel.remindersState.collectAsState()
    val selectedReminderId by viewModel.expandedReminderId.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    // Find selected reminder for overlay
    val selectedReminder = when (val state = remindersState) {
        is UiState.Success -> state.data.find { it.id == selectedReminderId }
        else -> null
    }

    val blurDp by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (selectedReminder != null) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "blurDp"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = SurfaceGray
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                if (selectedReminder == null) {
                    RemindersTopBar(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onNavigateBack = onNavigateBack,
                        isSearchActive = isSearchActive,
                        onSearchActiveChange = { isSearchActive = it }
                    )
                } else {
                    RemindersTitleTopBar(onNavigateBack = onNavigateBack)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (blurDp > 0.dp) Modifier.blur(blurDp) else Modifier)
                        // Tap outside search: if search is active and query is empty -> deactivate
                        .pointerInput(isSearchActive, searchQuery) {
                            detectTapGestures(
                                onTap = {
                                    // Always clear focus / hide keyboard when tapping content
                                    focusManager.clearFocus(force = true)
                                    keyboard?.hide()
                                    if (isSearchActive && searchQuery.isBlank()) {
                                        isSearchActive = false
                                    }
                                }
                            )
                        }
                ) {
                    when (val state = remindersState) {
                        is UiState.Loading -> {
                            RemindersLoadingState()
                        }
                        is UiState.Success -> {
                            val filteredReminders = if (searchQuery.isBlank()) {
                                state.data
                            } else {
                                state.data.filter { reminder ->
                                    reminder.title.contains(searchQuery, ignoreCase = true) ||
                                            reminder.tag.contains(searchQuery, ignoreCase = true) ||
                                            reminder.shortDescription.contains(searchQuery, ignoreCase = true)
                                }
                            }

                            if (filteredReminders.isEmpty()) {
                                EmptyState(message = stringResource(R.string.empty_reminders))
                            } else {
                                RemindersList(
                                    reminders = filteredReminders,
                                    onReminderClick = { viewModel.toggleExpanded(it) }
                                )
                            }
                        }
                        is UiState.Error -> {
                            ErrorState(
                                message = state.message,
                                onRetry = { viewModel.retry() }
                            )
                        }
                        is UiState.Empty -> {
                            EmptyState(message = stringResource(R.string.empty_reminders))
                        }
                    }
                }
            }
        }

        // Overlay dialog
        AnimatedVisibility(
            visible = selectedReminder != null,
            enter = fadeIn(animationSpec = tween(240, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = tween(240, easing = FastOutSlowInEasing)
                    ),
            exit = fadeOut(animationSpec = tween(200, easing = FastOutSlowInEasing)) +
                    scaleOut(
                        targetScale = 0.98f,
                        animationSpec = tween(200, easing = FastOutSlowInEasing)
                    )
        ) {

            ReminderOverlay(
                reminder = selectedReminder ?: return@AnimatedVisibility,
                onDismiss = { viewModel.collapseAll() }
            )
        }
    }
}

@Composable
private fun ReminderOverlay(
    reminder: Reminder,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val statusBarTop = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = statusBarTop + AppTopBarDefaults.Height + 12.dp)
                .shadow(10.dp, RoundedCornerShape(16.dp))
                .clickable(enabled = false) { },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF6F6)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TagChip(
                    text = stringResource(R.string.tag_format, reminder.tag),
                    backgroundColor = TagRedBackground,
                    textColor = TagChipText
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = reminder.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.calendar),
                        contentDescription = stringResource(R.string.calendar_icon_description),
                        tint = IconDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = reminder.date, fontSize = 14.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.width(14.dp))
                    Icon(
                        painter = painterResource(R.drawable.clock),
                        contentDescription = stringResource(R.string.clock_icon_description),
                        tint = IconDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = reminder.time, fontSize = 14.sp, color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = reminder.shortDescription, fontSize = 14.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(R.string.description_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(text = reminder.description, fontSize = 14.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.context_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(text = reminder.context, fontSize = 14.sp, color = TextPrimary)

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = stringResource(R.string.button_completed), color = Color.White)
                    }
                    Button(
                        onClick = {  },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = stringResource(R.string.button_edit), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun RemindersTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    AppTopBar(backgroundColor = Color.White, showDivider = true) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_description),
                tint = IconDark,
                modifier = Modifier.size(34.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD7ECFF))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onSearchActiveChange(true)
                    focusRequester.requestFocus()
                    keyboard?.show()
                }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_button_description),
                    tint = IconGray,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_hint),
                            color = TextHint,
                            fontSize = 15.sp
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(
                            color = TextPrimary,
                            fontSize = 15.sp
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RemindersTitleTopBar(onNavigateBack: () -> Unit) {
    AppTopBar(backgroundColor = Color.White, showDivider = true) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.reminders_title),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

@Composable
private fun RemindersList(
    reminders: List<Reminder>,
    onReminderClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        itemsIndexed(
            items = reminders,
            key = { _, reminder -> reminder.id }
        ) { index, reminder ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300, delayMillis = index * 50)) +
                        slideInVertically(
                            animationSpec = tween(300, delayMillis = index * 50),
                            initialOffsetY = { it / 2 }
                        )
            ) {
                ReminderCard(
                    reminder = reminder,
                    onClick = { onReminderClick(reminder.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tag
            TagChip(
                text = stringResource(R.string.tag_format, reminder.tag),
                backgroundColor = TagRedBackground,
                textColor = TagChipText
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = reminder.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date and Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = stringResource(R.string.calendar_icon_description),
                    tint = IconGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reminder.date,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = stringResource(R.string.clock_icon_description),
                    tint = IconGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = reminder.time,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Short Description
            Text(
                text = reminder.shortDescription,
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}


@Composable
private fun RemindersLoadingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .shimmer()
    ) {
        repeat(5) {
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerReminderCard()
        }
    }
}

@Composable
private fun ShimmerReminderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )
        }
    }
}
