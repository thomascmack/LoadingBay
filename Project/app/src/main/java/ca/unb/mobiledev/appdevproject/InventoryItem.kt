package ca.unb.mobiledev.appdevproject

/**
 * Represents a single item in the inventory.
 */

data class InventoryItem(
    val name: String,       // eg "square", "circle"
    val id: Long,       // unique per item of the same type
    var quantity: Int, // total count of items of this type
    var damaged: Int // count of damaged items of this type
)
