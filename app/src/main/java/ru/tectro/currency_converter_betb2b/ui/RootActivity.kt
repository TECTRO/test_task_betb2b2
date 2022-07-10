package ru.tectro.currency_converter_betb2b.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import ru.tectro.currency_converter_betb2b.datasources.api.CbrApi
import ru.tectro.currency_converter_betb2b.datasources.repo.IQuotesRepository
import ru.tectro.currency_converter_betb2b.datasources.util.Response
import ru.tectro.currency_converter_betb2b.ui.theme.Test_task_betb2bTheme
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class RootActivity : ComponentActivity() {

    @Inject
    lateinit var api: IQuotesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
           val u =  api.getQuotes()
        u
        }


        setContent {
            Test_task_betb2bTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Test_task_betb2bTheme {
        Greeting("Android")
    }
}