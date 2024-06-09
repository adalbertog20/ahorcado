package com.example.ahorcado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ahorcado.ui.theme.AhorcadoTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AhorcadoTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "menuscreen") {
                        composable("menuscreen") { MenuScreen(navController) }
                        composable(
                            route = "ahorcadoscreen/{word}",
                            arguments = listOf(navArgument("word") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            AhorcadoScreen(
                                navController,
                                word = backStackEntry.arguments?.getString("word") ?: "",
                                viewmodel = GameViewModel()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ahorcado", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("ahorcadoscreen/${getWord()}") }) {
            Text(text = "Iniciar Juego")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Adalberto Garcia")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Brandon Rodriguez")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Gabriel Casillas")
    }
}

@Composable
fun AhorcadoScreen(navController: NavHostController, word: String, viewmodel : GameViewModel) {
    var palabra by remember {
        mutableStateOf(word)
    }
    var wordArray by remember { mutableStateOf(palabra.toCharArray().toList()) }
    var wordState by remember { mutableStateOf(List(wordArray.size) { null as Char? }) }
    var lives by remember { mutableIntStateOf(3) }
    var showDialog by remember { mutableStateOf(false) }
    var points by rememberSaveable {
        mutableStateOf(0)
    }
    val rows = ('a'..'z').chunked(5)
    var guessedLetters by remember { mutableStateOf(setOf<Char>()) }

    if (lives <= 0) {
        showDialog = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Paper Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.85f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = palabra, fontSize = 50.sp, color = Color.Black)
        Text(text = "Puntaje: ${viewmodel.score}", fontSize = 50.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(
                id = when (lives) {
                    3 -> R.drawable.mono_1
                    2 -> R.drawable.mono_2
                    1 -> R.drawable.mono_3
                    else -> R.drawable.mono_4
                }
            ), contentDescription = "Ahorcado", modifier = Modifier.width(400.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = wordState.joinToString(separator = " ") { it?.toString() ?: "_" },
            fontSize = 50.sp,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(50.dp))
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        navController.navigate("ahorcadoscreen/${getWord()}")
                    }) {
                        Text(text = "Reiniciar")
                    }
                },
                title = { Text(text = "Perdiste Crack") },
                text = { Text(text = "Reiniciar") },
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in rows) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (i in row) {
                        Button(modifier = Modifier.padding(4.dp), onClick = {
                            if (wordArray.contains(i)) {
                                wordArray.forEachIndexed { index, c ->
                                    if (c == i) {
                                        wordState = wordState.toMutableList().apply {
                                            this[index] = i
                                            guessedLetters = guessedLetters + i
                                        }
                                    }
                                }
                                if(wordState == wordArray){
                                    var newPal = getWord()
                                    viewmodel.addScore()
                                    lives = 3
                                    showDialog = false
                                    wordArray = newPal.toCharArray().toList()
                                    wordState = List(wordArray.size) { null as Char?}
                                    guessedLetters = setOf<Char>()
                                    palabra = newPal
                                }
                            } else {
                                guessedLetters = guessedLetters + i
                                lives--
                            }
                        }, enabled = !guessedLetters.contains(i)) {
                            Text(text = i.toString())
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val navController = rememberNavController()
    AhorcadoTheme {
        AhorcadoScreen(navController, getWord(), GameViewModel())
    }
}

fun getWord(): String {
    val words = listOf(
        "algorithm",
        "function",
        "variable",
        "constant",
        "framework",
        "iteration",
        "recursion",
        "polymorphism",
        "encapsulation",
        "abstraction",
        "interface",
        "inheritance",
        "exception",
        "declaration",
        "compilation",
        "execution",
        "optimization",
        "virtualization",
        "serialization",
        "delegation",
        "concurrency",
        "multithreading",
        "dependency",
        "annotation",
        "reflection",
        "immutable",
        "singleton",
        "prototype",
        "middleware",
        "callback",
        "promises",
        "asynchronous",
        "synchronous",
        "deadlock",
        "racecondition",
        "semaphore",
        "mutex",
        "thread",
        "process",
        "microservice",
        "container",
        "docker",
        "kubernetes",
        "devops",
        "pipeline",
        "continuous",
        "deployment",
        "integration",
        "testing",
        "benchmark",
        "refactoring",
        "debugging",
        "performance",
        "scalability",
        "reusability"
    )
    return words[Random.nextInt(words.size)]
}