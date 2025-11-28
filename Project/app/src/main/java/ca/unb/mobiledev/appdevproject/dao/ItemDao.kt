package ca.unb.mobiledev.appdevproject.dao

import androidx.room.Dao
import androidx.room.Query
import ca.unb.mobiledev.appdevproject.entities.Item
import ca.unb.mobiledev.appdevproject.entities.Product
import ca.unb.mobiledev.appdevproject.entities.ProductName

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE shipmentID in (:shipmentID)")
    fun getShipment(shipmentID : Long): List<Item>

    @Query("SELECT itemName FROM item INNER JOIN product ON item.upc = product.upc WHERE product.upc in (:upc)")
    fun getProductName(upc : Long) : List<ProductName>
}