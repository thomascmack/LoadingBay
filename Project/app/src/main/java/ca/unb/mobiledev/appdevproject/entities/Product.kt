package ca.unb.mobiledev.appdevproject.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Product(
    @PrimaryKey val upc : Long,
    @ColumnInfo("itemName") val itemName : String
)

data class ProductName(
    @ColumnInfo("itemName") val itemName: String
)