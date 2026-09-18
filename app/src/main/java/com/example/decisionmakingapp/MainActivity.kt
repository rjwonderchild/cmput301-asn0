package com.example.decisionmakingapp

/*
Copyright [2026] [Riley Whitford]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
implied. See the License for the specific language governing permissions
and limitations under the License.
 */


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.decisionmakingapp.ui.theme.DecisionMakingAppTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DecisionMakingAppTheme {
                var currentImage by remember {
                    mutableStateOf(imageLibrary.random())
                }

                var randResult by remember {
                    mutableStateOf<String?>(null)
                }

                val restaurantStats = remember {
                    mutableStateMapOf<String, RestaurantStats>()
                }

                var showResults by remember {
                    mutableStateOf(false)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (showResults) {
                        ResultsScreen(
                            stats = restaurantStats,
                            goBack = {
                                showResults = false
                            }
                        )
                    } else {

                        ImageCard(
                            image = currentImage,
                            randResult = randResult,
                            onShowResults = {
                                showResults = true
                            },

                            yesGo = {
                                val result = getOutput(0.5)

                                val oldStats =
                                    restaurantStats[currentImage.name] ?: RestaurantStats()

                                restaurantStats[currentImage.name] =
                                    updateStats(oldStats, result)

                                randResult = result
                            },

                            yesMaybe = {
                                val result = getOutput(0.25)

                                val oldStats =
                                    restaurantStats[currentImage.name] ?: RestaurantStats()

                                restaurantStats[currentImage.name] =
                                    updateStats(oldStats, result)

                                randResult = result
                            },


                            yesNo = {
                                val result = getOutput(0.1)

                                val oldStats =
                                    restaurantStats[currentImage.name] ?: RestaurantStats()

                                restaurantStats[currentImage.name] =
                                    updateStats(oldStats, result)

                                randResult = result
                            },

                            goNext = {
                                currentImage = imageLibrary.random()
                                randResult = null
                            },

                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

// Helper function to detect yes or no for go to restaurant
// handles math probability.

    fun getOutput(yesGo: Double): String {
        return if (Math.random() < yesGo) {
            "Yes!"
        } else {
            "No!"
        }
    }

    // Helper function to change button colour when
// it is pressed.
    @Composable
    fun buttonColour(
        isClicked: Boolean,
        selectedColor: Color
    ) = ButtonDefaults.buttonColors(
        containerColor =
            if (isClicked) {
                selectedColor
            } else {
                MaterialTheme.colorScheme.secondary
            },

        contentColor = Color.White,

        disabledContainerColor =
            if (isClicked) {
                selectedColor
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },

        disabledContentColor = Color.White
    )

// Stats update function for each location

    fun updateStats(
        stats: RestaurantStats,
        result: String
    ): RestaurantStats {
        return if (result == "Yes!") {
            stats.copy(
                yesCount = stats.yesCount + 1
            )
        } else {
            stats.copy(
                noCount = stats.noCount + 1
            )
        }
    }

// Calculate percentage of yes to see location(s) stats

    fun calculateYes(stats: RestaurantStats): Double {
        val total = stats.yesCount + stats.noCount

        return if (total == 0) {
            0.0
        } else {
            stats.yesCount.toDouble() / total * 100.0
        }
    }

    @Composable
    fun ImageCard(
        image: ImageClass,
        randResult: String?,
        yesGo: () -> Unit,
        yesMaybe: () -> Unit,
        yesNo: () -> Unit,
        goNext: () -> Unit,
        onShowResults: () -> Unit,
        modifier: Modifier = Modifier
    ) {

        var clickedButton by remember {
            mutableStateOf<String?>(null)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = "Japan Restaurant Decision App",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
            )

            Image(
                painter = painterResource(id = image.imageId),
                contentDescription = image.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(top = 20.dp)
            )

            Text(
                text = image.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        clickedButton = "GO"
                        yesGo()
                    },
                    enabled = randResult == null,
                    colors = buttonColour(
                        clickedButton == "GO",
                        Color.Green
                    )
                ) {
                    Text("GO!")
                }

                Button(
                    onClick = {
                        clickedButton = "MAYBE"
                        yesMaybe()
                    },
                    enabled = randResult == null,
                    colors = buttonColour(
                        clickedButton == "MAYBE",
                        Color.Magenta
                    )
                ) {
                    Text("Maybe...")
                }

                Button(
                    onClick = {
                        clickedButton = "NO"
                        yesNo()
                    },
                    enabled = randResult == null,
                    colors = buttonColour(
                        clickedButton == "NO",
                        Color.Red
                    )
                ) {
                    Text("NO!")
                }
            }

            if (randResult != null) {
                Text(
                    text = randResult,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                Button(
                    onClick = {
                        clickedButton = null
                        goNext()
                    },

                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Next Restaurant")
                }

                Button(
                    onClick = onShowResults,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("View Results")
                }
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "ID: 1719413 | CCID: whitfor1",
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
        }
    }

@Composable
fun ResultsScreen(
    stats: Map<String, RestaurantStats>,
    goBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Restaurant Results",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        stats.forEach { (restaurantName, restaurantStats) ->

            val yesPercent = calculateYes(restaurantStats)

            Text(
                text = "$restaurantName: ${"%.1f".format(yesPercent)}% confidence to go.",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = goBack
        ) {
            Text("Back")
        }
    }
}
