package ca.unb.mobiledev.appdevproject.dao

import androidx.room.Dao
import androidx.room.Query
import ca.unb.mobiledev.appdevproject.entities.Item
import ca.unb.mobiledev.appdevproject.entities.ProductWithItems

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item JOIN product on item.upc = product.upc WHERE shipmentID in (:shipmentID)")
    fun getShipment(shipmentID : Long): List<ProductWithItems>
}