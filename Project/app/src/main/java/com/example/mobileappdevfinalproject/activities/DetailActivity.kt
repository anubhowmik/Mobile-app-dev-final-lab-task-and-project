package com.example.mobileappdevfinalproject.activities

import android.graphics.Paint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mobileappdevfinalproject.Cart.ManagementCart
import com.example.mobileappdevfinalproject.R
import com.example.mobileappdevfinalproject.databinding.ActivityDetailBinding
import com.example.mobileappdevfinalproject.model.ItemModel

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: ItemModel
    private lateinit var managementCart: ManagementCart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        managementCart = ManagementCart(this)
        item = intent.getSerializableExtra("object")!! as ItemModel
        setupViews()
    }
    private fun setupViews()=with(binding){
            titleTxt.text=item.title
            descriptionTxt.text=item.description
        priceTxt.text="$${item.price}"
        oldPriceTxt.text="$${item.oldPrice}"
        oldPriceTxt.paintFlags=priceTxt.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        ratingTxt.text="${item.rating} Rating"
        numberItemTxt.text=item.numberInCart.toString()
        Glide.with(this@DetailActivity)
            .load(item.picUrl.firstOrNull())
            .into(picMain)
        backBtn.setOnClickListener { finish() }
        plusBtn.setOnClickListener {
            item.numberInCart++
            numberItemTxt.text=item.numberInCart.toString()
            updateTotalPrice()
        }
        minusBtn.setOnClickListener {
            if(item.numberInCart>1) {
                item.numberInCart--
                numberItemTxt.text = item.numberInCart.toString()
                updateTotalPrice()
            }

        }
            addToCartBtn.setOnClickListener {
                managementCart.insertFood(item)
            }
        updateTotalPrice()
    }

    private fun updateTotalPrice() = with(binding) {
        val totalPrice= item.price * item.numberInCart
        totalPriceTxt.text="Total: $${totalPrice}"
    }

}