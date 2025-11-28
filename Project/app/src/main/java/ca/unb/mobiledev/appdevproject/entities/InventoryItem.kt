package ca.unb.mobiledev.appdevproject.entities

/**
 * Represents a single item in the inventory.
 */

data class InventoryItem(
    val name: String,       // eg "square", "circle"
    //val id: Long,       // unique number for identifying item
    val upc: Long,  //universal product code of item
    var damaged: Boolean // set to true if item came in damaged
)