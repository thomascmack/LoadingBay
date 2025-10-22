package ca.unb.mobiledev.appdevproject

/**
 * Groups inventory items dynamically by their type.
 */

class ItemList : ArrayList<InventoryItem>() {

    private var aggregateList = ArrayList<AggregateItem>()

    // Add an item to the manifest
    fun push(id : Long, name : String) {
        val item = InventoryItem(name, id, false)
        this.add(item)
        val i = hasItem(item)
        if(i >= 0) {
            aggregateList[i].count++
        }
        else {
            aggregateList.add(AggregateItem(id, name))
        }
    }

    fun pop() {
        if(this.isNotEmpty()) {
            val i = this.removeAt(this.size - 1)
            val iAggr = itemInAggr(i)
            iAggr!!.count --
            if(i.damaged) iAggr.countDamaged--
            if(iAggr.count == 0) aggregateList.remove(iAggr)
        }
    }

    fun setDamage(item: InventoryItem) {
        item.damaged = !item.damaged
        if(item.damaged) itemInAggr(item)!!.countDamaged++ else itemInAggr(item)!!.countDamaged--
    }

    fun itemInAggr(item : InventoryItem): AggregateItem? {
        for(aggrItem in aggregateList) {
            if(aggrItem.id == item.id) {
                return aggrItem
            }
        }
        return null
    }

    fun hasItem(item : InventoryItem): Int {
        return aggregateList.indexOf(itemInAggr(item))
    }

    fun top(): InventoryItem {
        return this.last()
    }

    fun getAggregate(): ArrayList<AggregateItem> {return aggregateList}

    data class AggregateItem(var id: Long, var name: String) {
        var count : Int = 1
        var countDamaged : Int = 0

        fun add(item: InventoryItem) {
            this.add(item)
            if(item.damaged) countDamaged++
        }
    }
}
