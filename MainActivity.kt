package com.moviesplus.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

private val Background = Color(0xFF090909)
private val SurfaceDark = Color(0xFF171717)
private val Accent = Color(0xFFE50914)
private val TextMain = Color(0xFFF5F5F5)
private val TextMuted = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MoviesPlusApp() }
    }
}

private enum class Tab(val label: String) {
    HOME("Home"), MOVIES("Movies"), MUSIC("Music"), REELS("Reels"), TV("Live TV"), PROFILE("Profile")
}

@Composable
private fun MoviesPlusApp() {
    var selected by remember { mutableStateOf(Tab.HOME) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var payload by remember { mutableStateOf(JSONObject()) }
    val scope = rememberCoroutineScope()

    fun load(tab: Tab = selected) {
        val endpoint = when (tab) {
            Tab.HOME -> "home"
            Tab.MOVIES -> "movies"
            Tab.MUSIC -> "music"
            Tab.REELS -> "reels"
            Tab.TV -> "tv"
            Tab.PROFILE -> "home"
        }
        scope.launch {
            loading = true
            error = null
            try { payload = ApiClient.get(endpoint) }
            catch (e: Exception) { error = e.message ?: "Unable to connect to API" }
            finally { loading = false }
        }
    }

    LaunchedEffect(selected) { load(selected) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Accent, background = Background, surface = SurfaceDark,
        onBackground = TextMain, onSurface = TextMain
    )) {
        Scaffold(
            containerColor = Background,
            topBar = {
                Row(
                    Modifier.fillMaxWidth().background(Background).padding(horizontal = 18.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("MOVIESPLUS", color = Accent, fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Text("Movies • Music • Reels • Live TV", color = TextMuted, fontSize = 11.sp)
                    }
                    IconButton(onClick = { load() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextMain)
                    }
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF111111), contentColor = TextMain) {
                    Tab.values().forEach { tab ->
                        val icon = when (tab) {
                            Tab.HOME -> Icons.Default.Home
                            Tab.MOVIES -> Icons.Default.Movie
                            Tab.MUSIC -> Icons.Default.MusicNote
                            Tab.REELS -> Icons.Default.SlowMotionVideo
                            Tab.TV -> Icons.Default.LiveTv
                            Tab.PROFILE -> Icons.Default.Person
                        }
                        NavigationBarItem(
                            selected = selected == tab,
                            onClick = { selected = tab },
                            icon = { Icon(icon, contentDescription = tab.label) },
                            label = { Text(tab.label, fontSize = 9.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                indicatorColor = Color(0xFF351014),
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = Accent)
                if (error != null) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CloudOff, null, tint = Accent, modifier = Modifier.size(42.dp))
                        Spacer(Modifier.height(10.dp))
                        Text("Could not load content", color = TextMain, fontWeight = FontWeight.Bold)
                        Text(error ?: "", color = TextMuted, fontSize = 12.sp)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { load() }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                            Text("Retry")
                        }
                    }
                } else {
                    when (selected) {
                        Tab.HOME -> HomeContent(payload)
                        Tab.MOVIES -> ListContent("Movies", ApiClient.dataArray(payload), "No movies returned by API")
                        Tab.MUSIC -> ListContent("Music", ApiClient.dataArray(payload), "No tracks returned by API")
                        Tab.REELS -> ListContent("Reels", ApiClient.dataArray(payload), "No reels returned by API")
                        Tab.TV -> ListContent("Live TV", ApiClient.dataArray(payload), "No channels returned by API")
                        Tab.PROFILE -> ProfilePlaceholder()
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeContent(json: JSONObject) {
    val sections = listOf(
        "featured_movies" to "Featured Movies",
        "trending_movies" to "Trending Now",
        "latest_movies" to "Latest Movies",
        "series" to "Web Series",
        "music" to "Music",
        "reels" to "Reels",
        "tv_channels" to "Live TV",
        "franchises" to "Explore Collections"
    )
    LazyColumn(contentPadding = PaddingValues(bottom = 18.dp)) {
        item {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text("YOUR ENTERTAINMENT HUB", color = TextMuted, fontSize = 11.sp, letterSpacing = 2.sp)
                Text("Discover something great.", color = TextMain, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Box(
                    Modifier.fillMaxWidth().background(
                        androidx.compose.ui.graphics.Brush.linearGradient(listOf(Color(0xFF5C080D), Color(0xFF171717))),
                        RoundedCornerShape(18.dp)
                    ).padding(18.dp)
                ) {
                    Column {
                        Text("MOVIESPLUS ORIGINAL EXPERIENCE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Movies, series, music and more.", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Content is loaded from your website API.", color = Color(0xFFD8D8D8), fontSize = 12.sp)
                    }
                }
            }
        }
        sections.forEach { (key, title) ->
            val array = ApiClient.rowsFromHome(json, key)
            if (array.length() > 0) {
                item { Text(title, Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp), color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold) }
                items(array.length()) { i ->
                    ContentRow(array.optJSONObject(i) ?: JSONObject(), key)
                }
            }
        }
        if (sections.all { ApiClient.rowsFromHome(json, it.first).length() == 0 }) {
            item {
                Text("API connected, but no home sections were returned. Check database table names and API response.",
                    Modifier.padding(20.dp), color = TextMuted)
            }
        }
    }
}

@Composable
private fun ListContent(title: String, rows: JSONArray, emptyText: String) {
    Column(Modifier.fillMaxSize()) {
        Text(title, Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        if (rows.length() == 0) {
            Text(emptyText, Modifier.padding(18.dp), color = TextMuted)
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(rows.length()) { i -> ContentRow(rows.optJSONObject(i) ?: JSONObject(), title.lowercase()) }
            }
        }
    }
}

@Composable
private fun ContentRow(item: JSONObject, section: String) {
    val title = item.optString("title").ifBlank {
        item.optString("name").ifBlank { item.optString("track_name").ifBlank { "Untitled" } }
    }
    val subtitle = listOfNotNull(
        item.optString("year").takeIf { it.isNotBlank() && it != "null" },
        item.optString("language").takeIf { it.isNotBlank() && it != "null" },
        item.optString("category_name").takeIf { it.isNotBlank() && it != "null" }
    ).joinToString(" • ")
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 5.dp)
            .background(SurfaceDark, RoundedCornerShape(12.dp)).padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(width = 48.dp, height = 62.dp).background(Color(0xFF292929), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Icon(
                when {
                    section.contains("music") -> Icons.Default.MusicNote
                    section.contains("reel") -> Icons.Default.SlowMotionVideo
                    section.contains("tv") -> Icons.Default.LiveTv
                    else -> Icons.Default.PlayArrow
                }, contentDescription = null, tint = Accent
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextMain, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle.isNotBlank()) Text(subtitle, color = TextMuted, fontSize = 12.sp)
            val overview = item.optString("overview").ifBlank { item.optString("description") }
            if (overview.isNotBlank() && overview != "null") Text(overview, color = TextMuted, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
    }
}

@Composable
private fun ProfilePlaceholder() {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Accent, modifier = Modifier.size(70.dp))
        Spacer(Modifier.height(12.dp))
        Text("Your MoviesPlus Account", color = TextMain, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("Login, registration, subscription and payment screens are the next integration step.", color = TextMuted)
    }
}
