package com.example.mobileappdevfinalproject.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobileappdevfinalproject.model.BrandModel
import com.example.mobileappdevfinalproject.model.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeRepository {
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _brands = MutableLiveData<MutableList<BrandModel>>()
    private val _banners = MutableLiveData<List<SliderModel>>()

    val brands: LiveData<MutableList<BrandModel>>
        get() = _brands

    val banners: LiveData<List<SliderModel>>
        get() = _banners

    fun loadBrands() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<BrandModel>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(BrandModel::class.java)?.let { list.add(it) }
                }
                _brands.value = list
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

   fun loadBanners() {
        val ref = firebaseDatabase.getReference("Banner")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SliderModel>()
                android.util.Log.d("HomeRepository", "Banners snapshot exists: ${snapshot.exists()}, children count: ${snapshot.childrenCount}")
                for (childSnapshot in snapshot.children) {
                    val slider = childSnapshot.getValue(SliderModel::class.java)
                    android.util.Log.d("HomeRepository", "Child snapshot: ${childSnapshot.value}")
                    android.util.Log.d("HomeRepository", "Mapped slider: $slider")
                    slider?.let { list.add(it) }
                }
                _banners.value = list
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("HomeRepository", "Banners load cancelled", error.toException())
            }
        })
    }
}
