package hrcode.labs.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hrcode.labs.calculator.model.CalculatorOperation
import hrcode.labs.calculator.viewmodel.CalculatorViewModel
import hrcode.labs.calculator.ui.theme.CalculatorTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.res.painterResource
import hrcode.labs.calculator.R
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.collectAsState

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel
) {
    val expression by viewModel.expression.observeAsState("")
    val history = viewModel.history.collectAsState(emptyList()).value
    val darkModeState = remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(16.dp))
                IconButton(onClick = { darkModeState.value = !darkModeState.value }) {
                    Icon(
                        painter = painterResource(id = if (darkModeState.value) R.drawable.brightness_7_24dp_e3e3e3_fill0_wght400_grad0_opsz24 else R.drawable.brightness_4_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = if (darkModeState.value) "Light Mode" else "Dark Mode",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) {
        CalculatorTheme(darkTheme = darkModeState.value) {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                topBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.menu_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculator", fontSize = 24.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = expression,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent),
                            readOnly = true
                        )
                    }
                    // History
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        history.takeLast(5).reversed().forEach { item ->
                            Text(
                                text = item.expression + " = " + item.result,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    // 4 columns x 5 rows grid
                    val buttons = listOf(
                        listOf("Erase","±","%", "/"),
                        listOf("7", "8", "9", "*"),
                        listOf("4", "5", "6", "-"),
                        listOf("1", "2", "3", "+"),
                        listOf("()","0", ".", "=")
                    )
                    buttons.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { label ->
                                val isOperation = label in listOf("+", "-", "*", "/", "%", "=")
                                CalculatorButton(label = label, onClick = {
                                    when (label) {
                                        "()" -> {
                                            val expr = expression
                                            val lastChar = if (expr.isNotEmpty()) expr.last() else null
                                            if (lastChar == null || lastChar in listOf('+', '-', '*', '/', '%', '(')) {
                                                viewModel.insertAtCursor("(", expr.length)
                                            } else {
                                                val openCount = expr.count { it == '(' }
                                                val closeCount = expr.count { it == ')' }
                                                if (openCount > closeCount) {
                                                    viewModel.insertAtCursor(")", expr.length)
                                                }
                                            }
                                        }
                                        "Erase" -> {
                                            viewModel.eraseAtCursor(expression.length - 1)
                                        }
                                        in "0".."9", "." -> {
                                            viewModel.insertAtCursor(label, expression.length)
                                        }
                                        "+" -> {
                                            viewModel.insertAtCursor("+", expression.length)
                                        }
                                        "-" -> {
                                            viewModel.insertAtCursor("-", expression.length)
                                        }
                                        "*" -> {
                                            viewModel.insertAtCursor("*", expression.length)
                                        }
                                        "/" -> {
                                            viewModel.insertAtCursor("/", expression.length)
                                        }
                                        "%" -> {
                                            viewModel.insertAtCursor("%", expression.length)
                                        }
                                        "=" -> viewModel.onCalculate()
                                        "±" -> viewModel.onOperationClick(CalculatorOperation.ChangeSign)
                                    }
                                }, isOperation = isOperation)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(label: String, onClick: () -> Unit, isOperation: Boolean = false) {
    val isBackspace = label == "Erase"
    val buttonColor = if (isOperation) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary
    val isDarkMode = MaterialTheme.colorScheme.background == Color(0xFF121212) // typical dark mode color
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isBackspace) {
            Icon(
                painter = painterResource(id = R.drawable.backspace_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            val textColor = if (isOperation && isDarkMode) Color.White else if (isOperation) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
            Text(text = label, fontSize = 24.sp, color = textColor)
        }
    }
}
