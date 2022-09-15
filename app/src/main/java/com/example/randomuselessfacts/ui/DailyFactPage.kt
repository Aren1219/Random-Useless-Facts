package com.example.randomuselessfacts.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun DailyFactPage(viewModel: MainViewModel){
    var showRandom by remember { mutableStateOf(false) }
    val dailyFact = viewModel.dailyFact.observeAsState()
    val randomFact = viewModel.randomFact.observeAsState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UiState(liveData = dailyFact, "Daily useless fact")
        RandomButton {
            showRandom = true
            viewModel.getRandomFact()
        }
        if (showRandom)
            UiState(liveData = randomFact,"Random useless fact")
    }
}

@Composable
fun UiState(liveData: State<Resource<Fact>?>, cardTitle:String) {
    when (liveData.value) {
        is Resource.Success<*> -> {
            FactCard(
                liveData.value!!.data!!,
                cardTitle
            )
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
fun FactCard(fact: Fact, title: String?) {
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
            ClickableText(
                modifier = Modifier.align(Alignment.End),
                text = annotatedString,
                onClick = { offset ->
                annotatedString.getStringAnnotations(tag = "url", start = offset, end = offset)
                    .firstOrNull()?.let {
                        uriHandler.openUri(it.item)
                    }
                }
            )
        }
    }
}

@Composable
fun RandomButton(onClick:() -> Unit){
    Button(onClick = {onClick()}) {
        Text(text = "Get random fact")
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDailyFact(){
    RandomUselessFactsTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FactCard(fact, "Daily useless fact")
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