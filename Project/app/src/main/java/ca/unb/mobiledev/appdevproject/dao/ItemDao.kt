package ca.unb.mobiledev.appdevproject.dao

import androidx.room.Dao
import androidx.room.Query
import ca.unb.mobiledev.appdevproject.entities.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE shipmentID in (:shipmentID)")
    fun getShipment(shipmentID : Long): List<Item>
}