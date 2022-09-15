package com.example.randomuselessfacts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.unit.dp
import com.example.randomuselessfacts.model.Fact
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SavedFactsPage(viewModel: MainViewModel, listState: LazyListState){

    val list = viewModel.savedFacts.observeAsState()
    
    if (list.value.isNullOrEmpty()){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Nothing here...")
        }
    } else {
        FactsList(list = list.value!!, listState = listState) { fact -> viewModel.deleteFact(fact) }
    }
    
}

@Composable
fun FactsList(
    list: List<Fact>,
    listState: LazyListState,
    delete: (Fact) -> Unit
){
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy((-8).dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ){
        items(list){ item ->
            FactCard(fact = item, title = null, Icons.Default.Delete) { delete(item) }
        }
    }
}