/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.bachors.pokemon.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.bachors.pokemon.R
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class MainActivity : ComponentActivity() {
    @SuppressLint("DiscouragedApi")
    @OptIn(ExperimentalGlideComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MaterialTheme {

                val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
                val swipeDismissableNavController = rememberSwipeDismissableNavController()

                val jsonType = readJsonFromAssets(this@MainActivity, "type.json")
                val typeList = parseTypeModel(jsonType)

                var sType: String? = null
                var sImage: String? = null

                SwipeDismissableNavHost(
                    navController = swipeDismissableNavController,
                    startDestination = "Type",
                    modifier = Modifier.background(MaterialTheme.colors.background)
                ) {
                    composable("Type") {
                        ScalingLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 28.dp,
                                start = 10.dp,
                                end = 10.dp,
                                bottom = 40.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            state = scalingLazyListState
                        ) {
                            items(typeList.size) { index ->
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 10.dp
                                        ),
                                    colors = ChipDefaults.chipColors(
                                        backgroundColor = colorResource(R.color.colorPrimary)
                                    ),
                                    icon = {
                                        val resID = resources.getIdentifier(
                                            typeList[index].title, "drawable",
                                            packageName
                                        )
                                        Image(
                                            painterResource(resID),
                                            contentDescription = "Type",
                                            modifier = Modifier
                                                .size(24.dp)
                                                .wrapContentSize(align = Alignment.Center),
                                        )
                                    },
                                    label = {
                                        Column {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        bottom = 2.dp
                                                    ),
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                text = typeList[index].title
                                                    .replaceFirstChar {
                                                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                                    }
                                            )
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                text = "Count " + typeList[index].count
                                            )
                                        }
                                    },
                                    onClick = {
                                        sType = typeList[index].title
                                        swipeDismissableNavController.navigate("List")
                                    }
                                )
                            }
                        }
                    }

                    composable("List") {
                            ScalingLazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(
                                    top = 28.dp,
                                    start = 10.dp,
                                    end = 10.dp,
                                    bottom = 40.dp
                                ),
                                verticalArrangement = Arrangement.Center,
                                state = scalingLazyListState
                            ) {
                                val jsonPokemon = readJsonFromAssets(this@MainActivity, "$sType.json")
                                val pokemonList = parsePokemonModel(jsonPokemon)
                                items(pokemonList.size) { index ->
                                    Chip(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 10.dp
                                            ),
                                        colors = ChipDefaults.chipColors(
                                            backgroundColor = colorResource(R.color.colorPrimary)
                                        ),
                                        icon = {
                                            GlideImage(
                                                model = pokemonList[index].image,
                                                contentDescription = pokemonList[index].name,
                                                modifier = Modifier
                                                    .wrapContentSize(align = Alignment.Center)
                                                    .size(40.dp)
                                            )
                                        },
                                        label = {
                                            Column {
                                                Text(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            top = 4.dp,
                                                            bottom = 2.dp
                                                        ),
                                                    color = Color.White,
                                                    text = pokemonList[index].name,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            bottom = 2.dp
                                                        ),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal,
                                                    text = "ID " + pokemonList[index].id
                                                )
                                                Text(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            bottom = 2.dp
                                                        ),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal,
                                                    text = "Weight " + pokemonList[index].weight
                                                )
                                                Text(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            bottom = 4.dp
                                                        ),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal,
                                                    text = "Height " + pokemonList[index].height
                                                )
                                            }
                                        },
                                        onClick = {
                                            sImage = pokemonList[index].image
                                            swipeDismissableNavController.navigate("Image")
                                        }
                                    )
                                }
                            }
                    }

                    composable("Image") {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Top
                        ) {
                            GlideImage(
                                model = sImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .wrapContentSize(align = Alignment.Center)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(8.dp)
                                    .size(200.dp)
                            )
                        }
                    }

                }
            }
        }
    }

    private fun readJsonFromAssets(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun parsePokemonModel(jsonString: String): List<Pokemon> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Pokemon>>() {}.type)
    }

    private fun parseTypeModel(jsonString: String): List<Type> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Type>>() {}.type)
    }

}