package com.example.randomuselessfacts.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.randomuselessfacts.model.Fact
import com.example.randomuselessfacts.ui.theme.RandomUselessFactsTheme
import com.example.randomuselessfacts.util.Resource

@Composable
fun DailyFactPage(viewModel: MainViewModel) {
    var showRandom by rememberSaveable { mutableStateOf(false) }

    val dailyFact = viewModel.dailyFact.observeAsState()
    val randomFact = viewModel.randomFact.observeAsState()

    val context = LocalContext.current

    var saved1 by rememberSaveable { mutableStateOf(false) }
    var saved2 by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UiState(
            liveData = dailyFact,
            cardTitle = "Daily Fact",
            { fact ->
                viewModel.saveFact(fact)
                Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT)
                    .show()
                saved1 = true
            },
            saved1
        )
        RandomButton {
            showRandom = true
            viewModel.getRandomFact()
            saved2 = false
        }
        if (showRandom)
            UiState(
                liveData = randomFact,
                cardTitle = "Random Fact",
                { fact ->
                    viewModel.saveFact(fact)
                    Toast.makeText(
                        context,
                        "Saved!",
                        Toast.LENGTH_SHORT
                    ).show()
                    saved2 = true
                },
                saved2
            )
    }
}

@Composable
fun UiState(
    liveData: State<Resource<Fact>?>,
    cardTitle: String,
    saveFact: (Fact) -> Unit,
    saved: Boolean
) {

    when (liveData.value) {
        is Resource.Success<*> -> {
            FactCard(
                liveData.value!!.data!!,
                cardTitle,
                if (saved) Icons.Default.Favorite
                else Icons.Default.FavoriteBorder
            ) {
                saveFact(liveData.value!!.data!!)
            }
        }
        is Resource.Loading -> {
            CircularProgressIndicator()
        }
        else -> {
            liveData.value?.message?.let { Text(text = it) }
        }
    }
}

@Composable
fun FactCard(
    fact: Fact,
    title: String?,
    buttonIcon: ImageVector,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = 16.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.h5
                )
            }
            Text(
                text = fact.text,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            val annotatedString = buildAnnotatedString {
                pushStringAnnotation(
                    tag = "url",
                    annotation = fact.sourceUrl,
                )
                withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
                    append(fact.source)
                }
                pop()
            }
            val uriHandler = LocalUriHandler.current
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
            ) {
                ClickableText(
                    modifier = Modifier.align(Alignment.TopStart),
                    text = annotatedString,
                    onClick = { offset ->
                        annotatedString.getStringAnnotations(
                            tag = "url",
                            start = offset,
                            end = offset
                        )
                            .firstOrNull()?.let {
                                try {
                                    uriHandler.openUri(it.item)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                    }
                )
                IconButton(
                    onClick = { onClick() },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(imageVector = buttonIcon, null)
                }
            }
        }
    }
}

@Composable
fun RandomButton(onClick: () -> Unit) {
    Button(onClick = { onClick() }) {
        Text(text = "Get random fact")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDailyFact() {
    RandomUselessFactsTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FactCard(fact, "Daily useless fact", Icons.Default.FavoriteBorder) {}
            RandomButton {}
        }
    }
}

val fact = Fact(
    "",
    "",
    "",
    "djtech.net",
    "http:\\/\\/www.djtech.net\\/humor\\/useless_facts.htm",
    "Months that begin on a Sunday will always have a `Friday the 13th`.",
)