package ru.tectro.quote_viewer_betb2b.main_screen.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import org.joda.time.LocalDate
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedField
import ru.tectro.quote_viewer_betb2b.domain.entities.SortedOrder
import ru.tectro.quote_viewer_betb2b.main_screen.ui.components.QuoteTab
import ru.tectro.quote_viewer_betb2b.main_screen.viewmodel.QuotesEvents
import ru.tectro.quote_viewer_betb2b.main_screen.viewmodel.QuotesViewModel
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: QuotesViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit, block = {
        if (!state.isQuotesLoading && state.quotes.isEmpty())
            viewModel.onEvent(QuotesEvents.LoadLatestQuotes)
    })
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var latestSelectedDate by remember {
        mutableStateOf<LocalDate?>(null)
    }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year: Int, month: Int, dayOfMonth: Int ->
                val selectedDate = LocalDate(
                    year,
                    month + 1,
                    dayOfMonth
                )
                if (selectedDate > LocalDate.now())
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(
                            "На ${selectedDate.toString("dd.MM.yyyy")} котировки еще не выпущены"
                        )
                    }
                else {
                    viewModel.onEvent(
                        QuotesEvents.LoadQuotesByDate(
                            selectedDate
                        )
                    )
                    latestSelectedDate = selectedDate
                }

            },
            state.date.year,
            state.date.monthOfYear - 1,
            state.date.dayOfMonth
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = "Главная") },
                actions = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }

                    IconButton(onClick = { viewModel.onEvent(QuotesEvents.NextSortedOrder) }) {
                        Icon(
                            imageVector = when (state.sortedOrder) {
                                SortedOrder.ASC -> Icons.Rounded.KeyboardArrowDown
                                SortedOrder.DESC -> Icons.Rounded.KeyboardArrowUp
                            },
                            contentDescription = null
                        )
                    }

                    var dropDownMenuState by remember { mutableStateOf(false) }

                    IconButton(onClick = { dropDownMenuState = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = dropDownMenuState,
                        onDismissRequest = { dropDownMenuState = false },
                    ) {
                        SortedField.values().forEach { field ->
                            DropdownMenuItem(onClick = {
                                viewModel.onEvent(
                                    QuotesEvents.SetSortedField(
                                        field
                                    )
                                )
                                dropDownMenuState = false
                            }) {
                                Text(
                                    text = "Сортировка по ${
                                        when (field) {
                                            SortedField.IsoNumCode -> "iso коду"
                                            SortedField.IsoCharCode -> "iso знаку"
                                            SortedField.Nominal -> "номиналу"
                                            SortedField.Title -> "имени"
                                            SortedField.Value -> "курсу"
                                            SortedField.IsFavourite -> "избранному"
                                        }
                                    }"
                                )
                            }
                        }
                    }
                }
            )
        }

    ) { padding ->
        Box(Modifier.padding(padding)) {

            LazyColumn {

                item {
                    state.owner?.let {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = 16.dp),
                            text = it,
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            text = state.date.toString("dd.MM.yyyy"),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }

                items(state.quotes, key = { it.id }) { quote ->
                    Box(
                        modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(300)
                        )
                    ) {
                        QuoteTab(quote = quote) {
                            viewModel.onEvent(
                                if (quote.isFavourite)
                                    QuotesEvents.RemoveFromFavorites(quote)
                                else
                                    QuotesEvents.AddToFavorites(quote)
                            )
                        }
                    }
                    Divider()
                }
            }

            AnimatedVisibility(
                visible = state.isQuotesLoading || state.quotesLoadingError != null,
                enter = fadeIn(tween(200)),
                exit = fadeOut(tween(200))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.background)
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedVisibility(
                        visible = state.isQuotesLoading,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(200))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Выполняется загрузка, ожидайте...",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = state.quotesLoadingError != null,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(200))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colors.background)
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Возникла непредвиденная ошибка.",
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                if (latestSelectedDate != null) {
                                    Button(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            latestSelectedDate?.let {
                                                viewModel.onEvent(
                                                    QuotesEvents.LoadQuotesByDate(
                                                        it
                                                    )
                                                )
                                                viewModel.onEvent(QuotesEvents.SuppressError)
                                            }
                                        }
                                    ) {
                                        Text(text = "Перезагрузить", textAlign = TextAlign.Center)
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                OutlinedButton(
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        viewModel.onEvent(QuotesEvents.LoadLatestQuotes)
                                        viewModel.onEvent(QuotesEvents.SuppressError)
                                    }) {
                                    Text(
                                        text = "Загрузить последнюю активную сводку",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}