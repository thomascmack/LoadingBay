package ca.unb.mobiledev.appdevproject.repositories

import ca.unb.mobiledev.appdevproject.dao.ProductDao
import ca.unb.mobiledev.appdevproject.entities.Product

class ProductRepository(private val pDao : ProductDao) {
    suspend fun findProduct(upc : Long) : List<Product>{
        return pDao.loadById(upc)
    }


}