package jones.software.rapidcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.material.color.DynamicColors
import jones.software.rapidcalculator.ui.theme.RapidCalculatorTheme
import java.text.NumberFormat
import kotlin.text.format

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RapidCalculatorTheme {
                DynamicColors.applyToActivityIfAvailable(this)
                RapidCalculatorApp()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RapidCalculatorApp() {
    val viewModel: ShoppingListViewModel = viewModel()
    val shoppingList by viewModel.shoppingList.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val salesTaxRate by viewModel.salesTaxRate.collectAsState()
    val total by viewModel.total.collectAsState()

    var itemName by remember { mutableStateOf(TextFieldValue("")) }
    var itemPrice by remember { mutableStateOf(TextFieldValue("")) }
    var itemQuantity by remember { mutableStateOf(TextFieldValue("")) }

    var taxRateText by remember { mutableStateOf(TextFieldValue("")) }
    var isTaxSet by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val resources = context.resources
    val configuration = resources.configuration
    val currentLocale =
        configuration.locales.get(0)


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Rapid Calculator") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isTaxSet) {
                        Text(
                            text = "Tax Rate: ${salesTaxRate * 100}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Button(onClick = { isTaxSet = false }) {
                            Text("Change")
                        }
                    } else {
                        OutlinedTextField(
                            value = taxRateText,
                            onValueChange = { taxRateText = it },
                            label = { Text("Tax Rate") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val taxRate = taxRateText.text.toDoubleOrNull() ?: 0.0
                                    viewModel.setSalesTaxRate(taxRate)
                                    isTaxSet = true
                                    focusManager.clearFocus()
                                }
                            )
                        )
                        Button(onClick = {
                            val taxRate = taxRateText.text.toDoubleOrNull() ?: 0.0
                            viewModel.setSalesTaxRate(taxRate)
                            isTaxSet = true
                            focusManager.clearFocus()
                        }) {
                            Text("Set Tax")
                        }
                    }
                }
            }

            // Top Section: Shopping List
            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Takes up the available space
                    .fillMaxWidth()
            ) {
                items(shoppingList) { item ->
                    ShoppingItemRow(item = item, viewModel = viewModel)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Section: Controls
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text("Item Name") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        )
                    )
                    OutlinedTextField(
                        value = itemPrice,
                        onValueChange = { itemPrice = it },
                        label = { Text("Price") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusManager.moveFocus(
                                    focusDirection = androidx.compose.ui.focus.FocusDirection.Right
                                )
                            }
                        )
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val name = itemName.text.replace("\n", "")
                                val price = itemPrice.text.toDoubleOrNull() ?: 0.0
                                val quantity = itemQuantity.text.toIntOrNull() ?: 1
                                if (name.isNotBlank()) {
                                    viewModel.addItem(
                                        ShoppingItem(
                                            name = name,
                                            price = price,
                                            quantity = quantity
                                        )
                                    )
                                    itemName = TextFieldValue("")
                                    itemPrice = TextFieldValue("")
                                    itemQuantity = TextFieldValue("")
                                }
                                focusManager.clearFocus()
                            }
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale)
                val formattedSubtotal = currencyFormatter.format(subtotal)
                val formattedTotal = currencyFormatter.format(total)


                Text("Subtotal: $formattedSubtotal", style = MaterialTheme.typography.headlineSmall)
                Text("Total: $formattedTotal", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemRow(item: ShoppingItem, viewModel: ShoppingListViewModel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
                Text(
                    text = "${item.quantity}x ${item.name} - $${item.price}",
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { viewModel.removeItem(item) }) {
                    Text("Remove")
                }
            }
        }
    }


