package ca.unb.mobiledev.appdevproject.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ca.unb.mobiledev.appdevproject.db.AppDatabase
import ca.unb.mobiledev.appdevproject.entities.Item
import ca.unb.mobiledev.appdevproject.entities.Product
import ca.unb.mobiledev.appdevproject.repositories.MyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: MyRepository

    // Expose the search results
    //  NOTE: This variable will be observed for change in the main activity
    val searchItems = MutableLiveData<List<Product>>()
    val manifestSearch = MutableLiveData<List<Item>>()

    init {
        val pDao = AppDatabase.getDatabase(application, viewModelScope).ProductDao()
        val iDao = AppDatabase.getDatabase(application, viewModelScope).ItemDao()
        repo = MyRepository(pDao, iDao)
    }

    fun search(upc : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            searchItems.postValue(repo.findProduct(upc))
        }
    }

    fun findManifest(shipmentID : Long) {
        viewModelScope.launch(Dispatchers.IO) {
            manifestSearch.postValue(repo.findManifest(shipmentID))
        }
    }
}