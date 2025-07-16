package hrcode.labs.calculator

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hrcode.labs.calculator.ui.HistoryScreen
import hrcode.labs.calculator.ui.theme.CalculatorTheme
import hrcode.labs.calculator.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val viewModel: CalculatorViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = hrcode.labs.calculator.viewmodel.CalculatorViewModelFactory(application as Application))
            val darkModeState = remember { mutableStateOf(false) }
            val drawerState = rememberDrawerState(DrawerValue.Closed)

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Text("Menu", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(16.dp))
                        IconButton(onClick = { darkModeState.value = !darkModeState.value }) {
                            Icon(
                                painter = painterResource(id = if (darkModeState.value) R.drawable.brightness_7_24dp_e3e3e3_fill0_wght400_grad0_opsz24 else R.drawable.brightness_4_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                contentDescription = if (darkModeState.value) "Light Mode" else "Dark Mode",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Calculator",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable { navController.navigate("calculator") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "History",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable { navController.navigate("history") }
                        )
                    }
                }
            ) {
                CalculatorTheme(darkTheme = darkModeState.value) {
                    NavHost(navController = navController, startDestination = "calculator") {
                        composable("calculator") {
                            hrcode.labs.calculator.ui.CalculatorScreen(viewModel = viewModel)
                        }
                        composable("history") {
                            hrcode.labs.calculator.ui.HistoryScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
