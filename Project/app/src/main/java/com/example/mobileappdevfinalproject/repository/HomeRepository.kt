package com.example.mobileappdevfinalproject.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mobileappdevfinalproject.model.BrandModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeRepository {
    private val firebaseDatabase= FirebaseDatabase.getInstance()

    private val _brands= MutableLiveData<MutableList<BrandModel>>()

    val brands: LiveData<MutableList<BrandModel>>
        get() = _brands

    fun loadBrands()
    {
        val ref= firebaseDatabase.getReference("Category")
        ref.addValueEventListener(listener = object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val list= mutableListOf<BrandModel>()
                for(childSnapshot in snapshot.children)
                {
                    childSnapshot.getValue(useExportFormat = BrandModel::class.java)?.let { list.add(it) }
                }

                _brands.value= list
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }




}