package jones.software.rapidcalculator


data class ShoppingItem(
    val id: Int = 0,
    val name: String,
    val quantity: Int,
    val price: Double
)

object IdGenerator {
    private var nextId = 1

    fun generateId(): Int {
        return nextId++
    }
}
