package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.mobileappdevfinalproject.Adapter.BrandsAdapter
import com.example.mobileappdevfinalproject.Adapter.PopularAdapter
import com.example.mobileappdevfinalproject.Adapter.SliderAdapter
import com.example.mobileappdevfinalproject.databinding.ActivityHomeBinding
import com.example.mobileappdevfinalproject.model.SliderModel
import com.example.mobileappdevfinalproject.utils.PreferenceManager
import com.example.mobileappdevfinalproject.viewmodel.HomeViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class HomeActivity : AppCompatActivity() {
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    private lateinit var binding: ActivityHomeBinding
    private val brandsAdapter = BrandsAdapter(mutableListOf())
    private val popularAdapter = PopularAdapter(mutableListOf())

    private lateinit var preferenceManager: PreferenceManager
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

        preferenceManager = PreferenceManager(this)

        // Show the logged-in user's email
        val userEmail = auth.currentUser?.email ?: "User"
        binding.tvWelcome.text = "Welcome!\n$userEmail"

        setupClickListeners()
    }

    private fun initUI() {
       initBrands()
        initBanners()
        initRecommended()

    }

    private fun initRecommended() {
        binding.recyclerViewRecommended.layoutManager= GridLayoutManager(this, 2)
        binding.recyclerViewRecommended.adapter=popularAdapter
        binding.progressBarRecommendation.visibility=View.VISIBLE

        viewModel.popular.observe(this) { data ->
            popularAdapter.updateDate(data)
            binding.progressBarRecommendation.visibility = View.GONE
        }
        viewModel.loadPopular()
    }

    private fun initBrands() {
        binding.recyclerViewBrands.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewBrands.adapter = brandsAdapter
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.brands.observe(this) { data ->
            brandsAdapter.updateData(data)
            binding.progressBarCategory.visibility = View.GONE
        }
        viewModel.loadBrands()
    }

    private fun setupBanners(image: List<SliderModel>){
        binding.viewpagerSlider.apply {
            adapter= SliderAdapter(image, this)
            clipToPadding=false
            clipChildren=false
            offscreenPageLimit=3
            (getChildAt(0) as RecyclerView).overScrollMode= RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(CompositePageTransformer().apply {
                    addTransformer(MarginPageTransformer(40))
            })
        }
        binding.dotIndicator.apply {
            visibility=if(image.size>1) View.VISIBLE else View.GONE
             if(image.size>1)attachTo(binding.viewpagerSlider)
        }
    }
    private fun initBanners() {
        binding.progressBarBanner.visibility = View.VISIBLE

        viewModel.banners.observe(this) { items ->
            android.util.Log.d("HomeActivity", "Banners received: ${items.size} items")
            items.forEachIndexed { index, sliderModel ->
                android.util.Log.d("HomeActivity", "Banner $index: url='${sliderModel.url}', picUrl='${sliderModel.picUrl}', image='${sliderModel.image}'")
            }
            setupBanners(items)
            binding.progressBarBanner.visibility = View.GONE
        }
        viewModel.loadBanners()
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        // Clear Remember Me preference
        preferenceManager.setRememberMe(false)
        // Sign out from Firebase
        auth.signOut()
        // Go back to Login screen
        startActivity(Intent(this, LoginActivity::class.java))
        // Clear all activities from the back stack
        finishAffinity()
    }

    // Prevent going back to Login when already logged in
    override fun onBackPressed() {
        super.onBackPressed()
        showLogoutConfirmation()
    }
}
