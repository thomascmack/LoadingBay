package ca.unb.mobiledev.appdevproject.repositories

import ca.unb.mobiledev.appdevproject.dao.ItemDao
import ca.unb.mobiledev.appdevproject.dao.ProductDao
import ca.unb.mobiledev.appdevproject.entities.ProductWithItems
import ca.unb.mobiledev.appdevproject.entities.Product

class MyRepository(private val pDao : ProductDao, private val iDao: ItemDao) {
    suspend fun findProduct(upc : Long) : List<Product>{
        return pDao.loadById(upc)
    }

    suspend fun findManifest(shipmentID : Long) : List<ProductWithItems>{
        return iDao.getShipment(shipmentID)
    }
}