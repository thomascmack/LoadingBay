package ca.unb.mobiledev.appdevproject

/**
 * Groups inventory items dynamically by their type.
 */

class ItemList : ArrayList<InventoryItem>() {

    private var itemStack = ArrayDeque<InventoryItem>()

    // Add an item to the manifest
    fun push(id : Int, name : String) {
        val index = hasItem(id)
        if(index >= 0) {
            this[index].quantity++
            itemStack.addLast(this[index])
        }
        else {
            this.add(InventoryItem(name, id, 1, 0))
            itemStack.addLast(this.last())
        }
    }

    fun pop() {
        if(this.isNotEmpty()) {
            val i = itemStack.removeLast()
            i.quantity--
            if(i.quantity < 1) this.remove(i)
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

    fun top(): InventoryItem {
        return itemStack.last()
    }
}
