package ca.unb.mobiledev.appdevproject

import ca.unb.mobiledev.appdevproject.entities.Item

class ItemList : ArrayList<Item>() {
    private var scanStack = ArrayList<ScanData>()
    private var maxItemID : Long = 0

    fun addItem(itemID : Long, upc : Long, flag : String = "Missing") {
        val item = Item(itemID,1, upc, flag, false, "")
        this.add(item)
        if(itemID > maxItemID) maxItemID = itemID
    }

    fun scanItem(upc : Long, itemName : String) {
        for(i in this) {
            if(i.upc == upc && i.flag == "Missing") {
                i.flag = "Received"
                scanStack.add(ScanData(upc, itemName, i.itemID))
                return
            }
        }
        maxItemID++
        this.addItem(maxItemID, upc, "Extra")
        scanStack.add(ScanData(upc, itemName, maxItemID))
    }

    fun undo() {
        if(scanStack.isNotEmpty()) {
            for(i in this) {
                if(i.itemID == scanStack.last().itemID) {
                    removeItem(i)
                    return
                }
            }
        }
    }

    fun getItem(itemID : Long) : Item? {
        for(i in this) {
            if(i.itemID == itemID) return i
        }
        return null
    }

    @Override
    fun isNotEmpty() : Boolean {
        return scanStack.isNotEmpty()
    }

    fun removeItem(i : Item) {
        for(s in scanStack) {
            if(s.itemID == i.itemID) {
                scanStack.remove(s)
                break
            }
        }
        if(i.flag == "Extra") {
            remove(i)
        }
        else {
            i.flag = "Missing"
        }
    }

    fun setDamage(itemID: Long) {
        for(i in this) {
            if(i.itemID == itemID) {
                i.damaged = !i.damaged
            }
        }
    }

    fun top(): ScanData {
        return scanStack.last()
    }

    data class ScanData(var upc : Long, var itemName : String, var itemID : Long = 0)
}
