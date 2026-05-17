package com.example.mobileappdevfinalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mobileappdevfinalproject.model.BrandModel
import com.example.mobileappdevfinalproject.model.SliderModel
import com.example.mobileappdevfinalproject.repository.HomeRepository

class HomeViewModel: ViewModel (){
    private val repository= HomeRepository()

    val brands: LiveData<MutableList<BrandModel>> = repository.brands
    val banners: LiveData<List<SliderModel>> = repository.banners
    fun loadBrands() = repository.loadBrands()
    fun loadBanners() = repository.loadBanners()
}