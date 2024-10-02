package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JettipappTheme
import com.example.jettipapp.widget.RoundIconButton


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val totalBillState = remember{
                mutableStateOf("0")
            }

            val validState = remember(totalBillState.value) {
                totalBillState.value.trim().isNotEmpty()
            }
            val slidePositionState = remember {
                mutableFloatStateOf(0f)
            }

            val tipPercentage = remember {
                mutableIntStateOf(0)
            }

            val splitByState = remember {
                mutableIntStateOf(1)
            }

            val tipAmountState = remember {
                mutableDoubleStateOf(0.0)
            }

            val totalPerPersonState = remember {
                mutableDoubleStateOf(0.0)
            }
            MyApp{
                Column {
                    TopHeader(totalPerPersonState.doubleValue)
                    BillForm(modifier = Modifier,totalBillState=totalBillState, slidePositionState = slidePositionState,
                        splitByState = splitByState , validState = validState , tipAmountState = tipAmountState,
                        tipPercentage = tipPercentage, totalPerPersonState = totalPerPersonState,onValChange = { newBillValue ->
                            // Handle the updated total bill value here
                            println("Total bill updated: $newBillValue")
                            totalPerPersonState.doubleValue = calculateTotalPerPerson(totalBill = totalBillState.value.toDouble(), splitBy = splitByState.intValue,tipPercentage = tipPercentage.intValue);
                            // You can add more logic here, for example, updating other state values
                        })

                }

            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit){
    JettipappTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp, vertical = 30.dp).height(150.dp).clip(shape = CircleShape.copy(all = CornerSize(12.dp))), color = Color(0xFFE9D7F7)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text("Total Per Person", style = MaterialTheme.typography.headlineSmall)
            Text("$$total",style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
        }
    }
}



@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange:(String) -> Unit = {},
    totalBillState:MutableState<String>,
    validState: Boolean,
    slidePositionState:MutableState<Float>,
    tipPercentage:MutableState<Int>,
    splitByState:MutableState<Int>,
    tipAmountState:MutableState<Double>,
    totalPerPersonState:MutableState<Double>){

    val keyboardController = LocalSoftwareKeyboardController.current


    Surface(modifier = Modifier.padding(2.dp).fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp),),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(modifier=Modifier.padding(6.dp)) {
            val focusManager = LocalFocusManager.current // To manage focus
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState) return@KeyboardActions

                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide()
                    focusManager.clearFocus()


                }
            )
               Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start){
                   Text("Split",modifier=Modifier.align(alignment = Alignment.CenterVertically))
                   Spacer(modifier = Modifier.width(120.dp))
                   Row(
                       modifier = Modifier.padding(horizontal = 3.dp),
                       verticalAlignment = Alignment.CenterVertically,
                       horizontalArrangement = Arrangement.Center
                   ){
                       RoundIconButton(
                           modifier = Modifier,
                           imageVector = Icons.Default.Remove,
                           onClick = {
                               if(splitByState.value >1){
                                   splitByState.value -= 1
                                   onValChange(totalBillState.value.trim())
                               }

                           }
                       )

                       Text(splitByState.value.toString(), modifier = Modifier.padding(horizontal = 7.dp))
                       RoundIconButton(
                           modifier = Modifier,
                           imageVector = Icons.Default.Add,
                           onClick = {
                               splitByState.value += 1
                               onValChange(totalBillState.value.trim())
                           }
                       )

                   }


               }
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(text = "Tip",modifier=Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier=Modifier.width(200.dp))

                    Text(text= "$${tipAmountState.value}", modifier = Modifier.align(alignment = Alignment.CenterVertically))
                }

                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "${tipPercentage.value} %")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value =slidePositionState.value , onValueChange = {
                        newVal -> slidePositionState.value = newVal
                        tipPercentage.value = (slidePositionState.value * 100).toInt()
                        tipAmountState.value = calculateTotalTip(totalBillState.value.toDouble(),tipPercentage.value)
                        onValChange(totalBillState.value.trim())


                    }, steps = 5)

                }

        }
    }
}

fun calculateTotalTip(totalBill: Double, tipPercentage: Int):Double {
    return if (totalBill > 1 && totalBill.toString().isNotEmpty()) ((totalBill*tipPercentage) / 100) else 0.0


}

fun calculateTotalPerPerson(totalBill:Double,splitBy:Int,tipPercentage: Int): Double{

    val bill = calculateTotalTip(totalBill = totalBill,tipPercentage = tipPercentage) + totalBill;

    return (bill / splitBy)

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JettipappTheme {

    }
}