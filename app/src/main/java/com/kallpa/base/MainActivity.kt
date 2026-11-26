package com.kallpa.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.kallpa.base.data.RoomClientRepository
import com.kallpa.base.data.local.KallpaDatabase
import com.kallpa.base.ui.MainScreen

class MainActivity : ComponentActivity() {
    private lateinit var database: KallpaDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(
            applicationContext,
            KallpaDatabase::class.java,
            KallpaDatabase.DATABASE_NAME,
        ).build()
        val repository = RoomClientRepository(database.clientDao())
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(repository = repository)
                }
            }
        }
    }

    override fun onDestroy() {
        database.close()
        super.onDestroy()
    }
}

@Composable
fun CounterScreen() {
    var count by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Greeting("KallpaBase")
        Text(text = "Clics: $count")
        Button(onClick = { count++ }) {
            Text(text = "Incrementar")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hola, $name!")
}

@Preview(showBackground = true)
@Composable
private fun CounterScreenPreview() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CounterScreen()
        }
    }
}
