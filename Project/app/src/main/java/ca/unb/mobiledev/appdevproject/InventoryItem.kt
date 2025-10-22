package ca.unb.mobiledev.appdevproject

/**
 * Represents a single item in the inventory.
 */

data class InventoryItem(
    val name: String,       // eg "square", "circle"
    val id: Long,       // unique per item of the same type
    var damaged: Boolean // set to true if item came in damaged
)
