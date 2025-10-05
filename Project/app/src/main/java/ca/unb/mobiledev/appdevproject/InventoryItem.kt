/**
 * Represents a single item in the inventory.
 */

data class InventoryItem(
    val type: String,       // eg "square", "circle"
    val itemNum: Int,       // unique per item of the same type
    val damaged: Boolean,   // true if item is damaged
)
