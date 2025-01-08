package jones.software.rapidcalculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ShoppingListViewModel :  ViewModel() {
    private val _shoppingList = MutableStateFlow<List<ShoppingItem>>(emptyList())
    val shoppingList: StateFlow<List<ShoppingItem>> = _shoppingList.asStateFlow()

    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    private val _salesTaxRate = MutableStateFlow(0.0)
    val salesTaxRate: StateFlow<Double> = _salesTaxRate.asStateFlow()

    private val _total = MutableStateFlow(0.0)
    val total: StateFlow<Double> = _total.asStateFlow()

    fun addItem(item: ShoppingItem) {
       val newItem = item.copy(id = IdGenerator.generateId())
        _shoppingList.update { it + newItem }
        calculateSubtotal()
        calculateTotal()
    }

    fun removeItem(item: ShoppingItem) {
        _shoppingList.update { it - item }
        calculateSubtotal()
        calculateTotal()
    }

    fun setSalesTaxRate(rate: Double) {
        _salesTaxRate.value = rate
        calculateTotal()
    }

    private fun calculateTotal() {
        val currentSubtotal = _subtotal.value
        val currentTaxRate = _salesTaxRate.value
        val taxAmount = currentSubtotal * currentTaxRate
        _total.value = currentSubtotal + taxAmount
    }

    private fun calculateSubtotal() {
        val newSubtotal = _shoppingList.value.sumOf { it.price * it.quantity }
        _subtotal.value = newSubtotal
    }

}