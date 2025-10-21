package ca.unb.mobiledev.appdevproject

/**
 * Groups inventory items dynamically by their type.
 */

class ItemList : ArrayList<InventoryItem>() {

    // Add an item to the manifest
    fun push(id : Int, name : String): InventoryItem {
        val index = hasItem(id)
        if(index >= 0) {
            this[index].quantity++
            this.add(this.removeAt(index))
        }
        else {
            this.add(InventoryItem(name, id, 1, 0))
        }
        return this.last()
    }

    fun pop() {
        if(this.isNotEmpty()) {
            this.last().quantity--
            if(this.last().quantity < 1) this.remove(this.last())
        }
    }
    fun hasItem(id : Int): Int {
        for (item in this) {
            if(item.id == id) {
                return this.indexOf(item)
            }
        }
        return -1
    }
}
