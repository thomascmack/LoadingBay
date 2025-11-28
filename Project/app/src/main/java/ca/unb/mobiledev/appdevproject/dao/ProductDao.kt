package ca.unb.mobiledev.appdevproject.dao

import androidx.room.Dao
import androidx.room.Query
import ca.unb.mobiledev.appdevproject.entities.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM product")
    fun getAll(): List<Product>

    @Query("SELECT * FROM product WHERE upc IN (:upc)")
    fun loadById(upc : Long): List<Product>
}