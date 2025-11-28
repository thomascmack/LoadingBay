package ca.unb.mobiledev.appdevproject

import ca.unb.mobiledev.appdevproject.entities.Item

/**
 * Groups inventory items dynamically by their type.
 */

class ItemList : ArrayList<Item>() {

    private var aggregateList = ArrayList<AggregateItem>()

    // TODO ItemID and ShipmentID functionality
    fun push(upc : Long) {
        val item = Item(1,1, upc, "Received", false, "")
        this.add(item)
        val i = hasItem(item)
        if(i >= 0) {
            aggregateList[i].count++
        }
        else {
            aggregateList.add(AggregateItem(upc))
        }
    }

    fun pop() {
        if(this.isNotEmpty()) {
            val i = this.removeAt(this.size - 1)
            val iAggr = itemInAggr(i)
            iAggr!!.count --
            if(i.flag == "Damaged") iAggr.countDamaged--
            if(iAggr.count == 0) aggregateList.remove(iAggr)
        }
    }

    fun setDamage(item: Item) {
        if(item.flag != "Damaged") {
            item.flag = "Damaged"
            itemInAggr(item)!!.countDamaged++
        }
        else {
            item.flag = "Arrived"
            itemInAggr(item)!!.countDamaged--
        }
    }

    fun itemInAggr(item : Item): AggregateItem? {
        for(aggrItem in aggregateList) {
            if(aggrItem.id == item.upc) {
                return aggrItem
            }
        }
        return null
    }

    fun hasItem(item : Item): Int {
        return aggregateList.indexOf(itemInAggr(item))
    }

    fun top(): Item {
        return this.last()
    }

    fun getAggregate(): ArrayList<AggregateItem> {return aggregateList}

    data class AggregateItem(var id: Long) {
        var count : Int = 1
        var countDamaged : Int = 0

        fun add(item: Item) {
            this.add(item)
            if(item.flag == "Damaged") countDamaged++
        }
    }
}
