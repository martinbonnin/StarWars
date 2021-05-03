package dev.johnoreilly.starwars.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat.setDecorFitsSystemWindows
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import dev.johnoreilly.starwars.shared.StarWarsRepository
import fragment.Person
import dev.johnoreilly.starwars.androidApp.theme.StarWarsTheme
import dev.johnoreilly.starwars.shared.model.Film

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDecorFitsSystemWindows(window, false)

        setContent {
            StarWarsTheme {
                ProvideWindowInsets {
                    MainLayout()
                }
            }
        }
    }
}

sealed class Screen(val title: String) {
    object PersonList : Screen("Person List")
    object FilmList : Screen("Film List")
}

data class BottomNavigationitem(
        val route: String,
        val icon: Int,
        val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationitem(Screen.PersonList.title, R.drawable.ic_face, Screen.PersonList.title),
    BottomNavigationitem(Screen.FilmList.title, R.drawable.ic_movie, Screen.FilmList.title)
)

@Composable
fun MainLayout() {
    val navController = rememberNavController()

    val repo = remember { StarWarsRepository() }
    val people by repo.getPeople().collectAsState(emptyList())
    val filmList by repo.getFilms().collectAsState(emptyList())

    Scaffold(
        topBar = { StarWarsTopAppBar("Star Wars") },
        bottomBar = { StarWarsBottomNavigation(navController) }
    ) {

        NavHost(navController, startDestination = Screen.PersonList.title) {
            composable(Screen.PersonList.title) {
                PeopleList(people)
            }
            composable(Screen.FilmList.title) {
                FilmList(filmList)
            }
        }
    }
}



@Composable
private fun StarWarsTopAppBar(title: String) {
    Surface(color = MaterialTheme.colors.primary) {
        TopAppBar(
            title = { Text(title) },
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            modifier = Modifier.statusBarsPadding()
        )
    }
}

@Composable
private fun StarWarsBottomNavigation(navController: NavHostController) {

    BottomNavigation(modifier = Modifier.navigationBarsPadding()) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

         bottomNavigationItems.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(item.icon), contentDescription = item.iconContentDescription) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo = navController.graph.startDestination
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun PeopleList(people: List<Person>) {
    LazyColumn {
        items(people) { person ->
            PersonView(person)
        }
    }
}

@Composable
fun PersonView(person: Person) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column {
            Text(text = person.name ?: "", style = MaterialTheme.typography.h6)
            Text(
                text =person.homeworld?.name ?: "",
                style = MaterialTheme.typography.caption.copy(color = Color.DarkGray)
            )
        }
    }
    Divider()
}


@Composable
fun FilmList(filmList: List<Film>) {
    LazyColumn {
        items(filmList) { film ->
            FilmView(film)
        }
    }
}


@Composable
fun FilmView(film: Film) {
    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column {
            Text(film.title, style = MaterialTheme.typography.h6)
            Text(film.director, style = MaterialTheme.typography.caption.copy(color = Color.DarkGray))
        }
    }
    Divider()
}