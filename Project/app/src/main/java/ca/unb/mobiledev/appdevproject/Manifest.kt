/**
 * Groups inventory items dynamically by their type.
 */

object Manifest {
    private val typeMap = mutableMapOf<String, MutableList<InventoryItem>>()

    // Add an item to the manifest under its type
    fun addItem(item: InventoryItem) {
        val list = typeMap.getOrPut(item.type) { mutableListOf() }
        list.add(item)

        // should add check to ensure no 2 items have same type and uid
    }

    // Get all items of a given type (eg "square")
    fun getItemsByType(type: String): List<InventoryItem> {
        return typeMap[type] ?: emptyList()
    }

    // Get all types currently in the manifest
    fun getAllTypes(): Set<String> {
        return typeMap.keys
    }

    // Get the entire manifest map
    fun getAllItems(): Map<String, List<InventoryItem>> {
        return typeMap
    }
}
