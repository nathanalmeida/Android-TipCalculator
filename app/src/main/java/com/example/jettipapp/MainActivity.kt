package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetTipAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.background) {
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
//        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            ) {
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person",
                style = MaterialTheme.typography.headlineSmall)
            Text(text = "$$total",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Preview
@Composable
fun MainContent() {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val splitByState = remember {
        mutableIntStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 10)
    Column {
        BillForm(totalBillState = totalBillState,
            sliderPositionState = sliderPositionState,
            splitByState = splitByState,
            range = range,
            ) { }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             totalBillState: MutableState<String>,
             sliderPositionState: MutableFloatState,
             splitByState: MutableIntState,
             range: IntRange,
             onValueChanged: (String) -> Unit = {}
             ) {

    val validState = totalBillState.value.trim().isNotEmpty()
    val keyboardController = LocalSoftwareKeyboardController.current

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val totalTip = calculateTotalTip(totalBill = "0${totalBillState.value}".toDouble(),
        tipPercentage = tipPercentage)

    val totalPerPerson = calculateTotalPerPerson(
        totalBill = "0${totalBillState.value}".toDouble(),
        splitBy = splitByState.value,
        tipPercentage = tipPercentage)
    TopHeader(totalPerPerson = totalPerPerson)

    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier = modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChanged(totalBillState.value.trim())

                    keyboardController?.hide()
                }
            )
            if (validState) {
                Row (modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                        modifier = modifier.align(
                            alignment = Alignment.CenterVertically
                        ))
                    Spacer(modifier = modifier.width(120.dp))
                    Row(modifier = modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByState.value > range.first) {
                                    splitByState.value--
                                }
                            })
                        Text(text = "${splitByState.value}", modifier = modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 9.dp, end = 9.dp))
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value++
                                }
                            })
                    }
                }

                //Tip Row
                Row (modifier = modifier.padding(horizontal = 3.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Total Tip",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = modifier.width(160.dp))
                    Text(text = "$ $totalTip", //"$ ${tipAmountState.value}",
                        modifier = modifier.align(alignment = Alignment.CenterVertically))
                }

                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = modifier.height(14.dp))

                    //Slider
                    Slider(modifier = modifier.padding(start = 16.dp, end = 16.dp),
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                        },
                        steps = 5)
                }
            } else {
                Box {}
            }

        }
    }
}