package com.example.mobileappdevfinalproject.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappdevfinalproject.Adapter.CartAdapter
import com.example.mobileappdevfinalproject.Cart.ChangeNumberItemsListener
import com.example.mobileappdevfinalproject.Cart.ManagementCart
import com.example.mobileappdevfinalproject.R
import com.example.mobileappdevfinalproject.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var managementCart: ManagementCart
    private var tax: Double= 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        managementCart= ManagementCart(this)

        initView()
        initCartList()
        calculateCart()
        }

    private fun initView() {
        binding.backBtn.setOnClickListener { finish() }
        binding.button.setOnClickListener {
            val intent = android.content.Intent(this, CheckoutActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initCartList(){
        binding.apply{
            viewCart.layoutManager= LinearLayoutManager(this@CartActivity,LinearLayoutManager.VERTICAL,false)

            viewCart.adapter= CartAdapter(managementCart.getListCart(), this@CartActivity, object :
                ChangeNumberItemsListener {
                override fun onChanged() {
                    calculateCart()

                }

            })

            emptyTxt.visibility=if(managementCart.getListCart().isEmpty()) View.VISIBLE else View.GONE

            scrollView3.visibility=
                if(managementCart.getListCart().isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun calculateCart(){
        val percentTax=0.02
        val delivery=10.0
        tax= managementCart.getTotalFee() * percentTax
        val total= managementCart.getTotalFee() + tax + delivery
        val itemTotal= managementCart.getTotalFee()
        with(binding){
            totalFeeTxt.text="$$itemTotal"
            taxTxt.text="$$tax"
            deliveryTxt.text="$$delivery"
            totalTxt.text="$$total"
        }
    }
}
