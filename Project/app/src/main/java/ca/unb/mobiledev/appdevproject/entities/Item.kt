package ca.unb.mobiledev.appdevproject.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * Represents a single item in the inventory.
 */

@Entity(tableName = "item", foreignKeys = [ForeignKey(
    entity = Product::class,
    parentColumns = ["upc"],
    childColumns = ["upc"]
)])

data class Item(
    @PrimaryKey val itemID : Long,
    @ColumnInfo("shipmentID") val shipmentID : Long,
    @ColumnInfo("upc") val upc : Long,
    @ColumnInfo("flag") var flag : String,
    @ColumnInfo("damaged", defaultValue = "0") var damaged : Boolean = false,
    @ColumnInfo("description") val description : String? = ""
)

data class ProductWithItems(
    @Embedded val
    product : Product,
    @Relation(
        parentColumn = "upc",
        entityColumn = "upc"
    )
    val items: List<Item>
)
