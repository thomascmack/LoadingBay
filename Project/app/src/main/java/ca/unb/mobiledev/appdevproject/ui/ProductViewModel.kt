package ca.unb.mobiledev.appdevproject.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.unb.mobiledev.appdevproject.db.AppDatabase
import ca.unb.mobiledev.appdevproject.entities.Product
import ca.unb.mobiledev.appdevproject.repositories.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val pRepo: ProductRepository

    // Expose the search results
    //  NOTE: This variable will be observed for change in the main activity
    val searchItems = MutableLiveData<List<Product>>()

    init {
        val itemDao = AppDatabase.getDatabase(application, viewModelScope).ProductDao()
        pRepo = ProductRepository(itemDao)
        Log.i("viewmodel", "init")
    }

    fun search(upc : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            searchItems.postValue(pRepo.findProduct(upc))
        }
    }
}