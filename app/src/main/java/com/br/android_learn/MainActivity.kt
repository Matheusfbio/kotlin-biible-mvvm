package com.br.android_learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.br.android_learn.data.api.RetrofitInstance
import com.br.android_learn.repository.BibleRepository
import com.br.android_learn.ui.screen.BibleVerseScreen
import com.br.android_learn.ui.theme.AndroidlearnTheme
import com.br.android_learn.viewModel.BibleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = BibleRepository(RetrofitInstance.api)
        val viewModel = BibleViewModel(repository)

        setContent {
            BibleVerseScreen(viewModel)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidlearnTheme {
        Greeting("Android")
    }
}