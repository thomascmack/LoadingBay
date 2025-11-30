package ca.unb.mobiledev.appdevproject.classes

import android.util.Log
import ca.unb.mobiledev.appdevproject.entities.Item
import ca.unb.mobiledev.appdevproject.entities.Product
import ca.unb.mobiledev.appdevproject.entities.ProductWithItems

class ProductList(val shipmentID : Long,
                  var maxItemID : Long,
                  val scanStack: ArrayList<ScanData> = ArrayList()) : ArrayList<ProductWithItems>() {
    @Override
    override fun add(e: ProductWithItems): Boolean {
        if(getProduct(e.product.upc) != null) return false
        for(i in e.items) {
            i.flag = "Missing"
        }
        return super.add(e)
    }

    fun scanItem(upc : Long, itemName : String) {
        val p = getProduct(upc)
        p?.let {
            addItem(upc, itemName, p.items)
            return
        }
        val newP = Product(upc, itemName)
        maxItemID++
        val newI = MutableList(1) {Item(maxItemID, shipmentID, upc, "Extra")}
        this.add(ProductWithItems(newP, newI))
        scanStack.add(ScanData(upc, itemName, maxItemID))
    }

    fun undo() {
        if(scanStack.isEmpty()) return
        val p = getProduct(scanStack.last().upc)
        p?.let { removeItem(scanStack.last().itemID, p.items) }
        scanStack.removeAt(scanStack.size - 1)
    }

    @Override
    fun isNotEmpty() : Boolean {
        return scanStack.isNotEmpty()
    }

    fun setDamage(item: Item?) {
        item?.let { item.damaged = !item.damaged }
    }

    fun setDescription(item: Item?, description: String) {
        item?.description = description
    }

    fun top(): Item? {
        return getItem(scanStack.last())
    }

    fun getItem(upc : Long, itemID : Long) : Item? {
        for(i in getProduct(upc)?.items!!) {
            if(i.itemID == itemID) return i
        }
        return null
    }

    fun getItem(scanData : ScanData) : Item? {
        return getItem(scanData.upc, scanData.itemID)
    }

    fun getItemName(item: Item?) : String? {
        item?.let {return getProduct(item.upc)?.product?.itemName }
        return ""
    }

    fun getProduct(upc : Long): ProductWithItems? {
        for(p in this) {
            if(p.product.upc == upc) return p
        }
        return null
    }

    fun addItem(upc : Long, itemName : String, items : MutableList<Item>) {
        for(i in items) {
            if(i.flag == "Missing") {
                i.flag = "Received"
                scanStack.add(ScanData(upc, itemName, i.itemID))
                return
            }
        }
        maxItemID++
        items.add(Item(maxItemID, shipmentID, upc, "Extra"))
        scanStack.add(ScanData(upc, itemName, maxItemID))
    }

    fun removeItem(pos : Int, items : MutableList<Item>) {
        if(items[pos].flag == "Extra") {
            removeFromStack(items[pos].itemID)
            items.removeAt(pos)
        }
        else {
            for(i in items.subList(pos, items.size)) {
                if (i.flag == "Extra") {
                    items[pos].damaged = false
                    items[pos].description = ""
                    removeFromStack(i.itemID)
                    items.remove(i)
                    return
                }
            }
            removeFromStack(items[pos].itemID)
            items[pos].flag = "Missing"
            items[pos].damaged = false
            items[pos].description = ""
        }
    }

    fun removeItem(itemID : Long, items : MutableList<Item>) {
        for(i in items) {
            if(i.itemID == itemID) {
                if(i.flag == "Extra") {
                    items.remove(i)
                    return
                }
                else {
                    i.flag = "Missing"
                    i.damaged = false
                    i.description = ""
                    return
                }
            }
        }
    }

    fun removeFromStack(itemID: Long) {
        for(s in scanStack) {
            if(s.itemID == itemID) {
                scanStack.remove(s)
                return
            }
        }
    }

    fun countTotal(upc : Long) : Int {
        val p = getProduct(upc)
        p?.let { return p.items.size }
        return 0
    }

    fun countExpected(upc : Long) : Int {
        var count = 0
        val p = getProduct(upc)
        p?.let {
            for(i in p.items)
                if(i.flag != "Extra") count++
        }
        return count
    }

    fun countReceived(upc : Long) : Int {
        var count = 0
        val p = getProduct(upc)
        p?.let {
            for(i in p.items)
                if(i.flag != "Missing") count++
        }
        return count
    }

    fun countDamaged(upc : Long) : Int {
        var count = 0
        val p = getProduct(upc)
        p?.let {
            for(i in p.items)
                if(i.damaged) count++
        }
        return count
    }

    fun totalDamaged() : Int {
        var count = 0
        for(p in this) {
            for (i in p.items) {
                if (i.damaged) count++
            }
        }
        return count
    }

    fun totalMissing() : Int {
        var count = 0
        for(p in this) {
            for (i in p.items) {
                if (i.flag == "Missing") count++
            }
        }
        return count
    }

    fun totalExtra() : Int {
        var count = 0
        for(p in this) {
            for (i in p.items) {
                if (i.flag == "Extra") count++
            }
        }
        return count
    }

    data class ScanData(var upc : Long, var itemName : String, var itemID : Long = 0)
}