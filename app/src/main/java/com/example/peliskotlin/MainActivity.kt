package com.example.peliskotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.peliskotlin.ui.theme.PelisKotlinTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    @ExperimentalPagerApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchByPage(1)
        searchByPage(2)
        Thread.sleep(500)
        setContent {
            PelisKotlinTheme {
                MainScreen()
            }
        }
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MainScreen() {
    var state by remember { mutableStateOf(0) }
    val titles = listOf("Movies", "Series")
    Column {
        TopBar()
        TabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = state == index,
                    onClick = { state = index }
                )
            }
        }
        when (state) {
            0 -> {
                RecyclerMovies(mutableList.toList())
            }
            else -> {

            }
        }
    }
}

private fun getRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/movie/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

val mutableList = mutableListOf<MoviesResponse>()

@ExperimentalMaterialApi
@ExperimentalPagerApi
private fun searchByPage(page: Int) {
    mutableList.clear()
    CoroutineScope(Dispatchers.IO).launch {
        val call = getRetrofit().create(APIService::class.java)
            .getMovies("popular?api_key=a836e9d662c1e554656d4cb03fe5d289&language=es-ES&page=$page")
        val moviesByPage = call.body()
        if (call.isSuccessful) {
            val images = moviesByPage?.results ?: emptyList()
            mutableList.addAll(images)
        } else {
            println("Error al consumir API")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecyclerMovies(list: List<MoviesResponse>) {
    Column {
        LazyVerticalGrid(cells = GridCells.Fixed(3)) {
            items(list) { movie ->
                ItemMovie(movie)
            }
        }
    }
}

@Composable
fun ItemMovie(movies: MoviesResponse) {
    val build = "https://image.tmdb.org/t/p/w500" + movies.poster_path
    Card(
        modifier = Modifier.padding(5.dp)
    ) {
        Image(
            painter = rememberImagePainter(build),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PelisKotlinTheme() {
        MainScreen()
    }
}

@Composable
fun TopBar() {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name), fontSize = 18.sp) },
        contentColor = Color.White
    )
}