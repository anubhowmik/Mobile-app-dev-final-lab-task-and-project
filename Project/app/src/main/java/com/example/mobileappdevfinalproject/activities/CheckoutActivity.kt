package com.example.mobileappdevfinalproject.activities


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileappdevfinalproject.Cart.ManagementCart
import com.example.mobileappdevfinalproject.databinding.ActivityCheckoutBinding
import com.example.mobileappdevfinalproject.model.OrderItemModel
import com.example.mobileappdevfinalproject.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var managementCart: ManagementCart
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        managementCart = ManagementCart(this)

        displayOrderSummary()
        setupClickListeners()
    }

    private fun displayOrderSummary() {
        val percentTax = 0.02
        val delivery = 10.0
        val subtotal = managementCart.getTotalFee()
        val tax = subtotal * percentTax
        val total = subtotal + tax + delivery

        binding.apply {
            summarySubtotalTxt.text = "$${"%.2f".format(subtotal)}"
            summaryTaxTxt.text = "$${"%.2f".format(tax)}"
            summaryDeliveryTxt.text = "$${"%.2f".format(delivery)}"
            summaryTotalTxt.text = "$${"%.2f".format(total)}"
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener { finish() }

        binding.placeOrderBtn.setOnClickListener {
            if (validateInputs()) {
                placeOrder()
            }
        }
    }

    private fun validateInputs(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val address = binding.etAddress.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val postal = binding.etPostalCode.text.toString().trim()

        if (fullName.isEmpty()) {
            binding.etFullName.error = "Full name is required"
            return false
        }
        if (phone.isEmpty() || phone.length < 10) {
            binding.etPhone.error = "Valid phone number is required"
            return false
        }
        if (address.isEmpty()) {
            binding.etAddress.error = "Address is required"
            return false
        }
        if (city.isEmpty()) {
            binding.etCity.error = "City is required"
            return false
        }
        if (postal.isEmpty()) {
            binding.etPostalCode.error = "Postal code is required"
            return false
        }
        return true
    }

    private fun placeOrder() {
        binding.placeOrderBtn.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE

        val user = auth.currentUser
        val percentTax = 0.02
        val delivery = 10.0
        val subtotal = managementCart.getTotalFee()
        val tax = subtotal * percentTax
        val total = subtotal + tax + delivery

        // Convert cart items to OrderItemModel list (serialisable for Firestore)
        val orderItems = managementCart.getListCart().map {
            OrderItemModel(
                title = it.title,
                price = it.price,
                quantity = it.numberInCart,
                picUrl = it.picUrl.firstOrNull() ?: ""
            )
        }

        // Determine payment method
        val paymentMethod = when (binding.rgPayment.checkedRadioButtonId) {
            binding.rbCard.id -> "Card"
            binding.rbBkash.id -> "bKash"
            else -> "Cash on Delivery"
        }

        val orderId = UUID.randomUUID().toString()
        val order = OrderModel(
            orderId = orderId,
            userId = user?.uid ?: "",
            userEmail = user?.email ?: "",
            fullName = binding.etFullName.text.toString().trim(),
            phone = binding.etPhone.text.toString().trim(),
            address = binding.etAddress.text.toString().trim(),
            city = binding.etCity.text.toString().trim(),
            postalCode = binding.etPostalCode.text.toString().trim(),
            paymentMethod = paymentMethod,
            items = orderItems,
            subtotal = subtotal,
            tax = tax,
            delivery = delivery,
            total = total,
            status = "Pending",
            timestamp = System.currentTimeMillis()
        )

        val db = FirebaseDatabase.getInstance().reference
        // Save under /orders/{orderId}  — admin reads from here
        db.child("orders").child(orderId).setValue(order)
            .addOnSuccessListener {
                // Also write a lightweight notification node for admin
                val notification = mapOf(
                    "orderId" to orderId,
                    "userEmail" to (user?.email ?: ""),
                    "total" to total,
                    "status" to "Pending",
                    "timestamp" to System.currentTimeMillis()
                )
                db.child("admin_notifications").child(orderId).setValue(notification)

                // Clear the cart after successful order
                managementCart.clearCart()

                binding.progressBar.visibility = View.GONE
                navigateToConfirmation(orderId, total)
            }
            .addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.placeOrderBtn.isEnabled = true
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun navigateToConfirmation(orderId: String, total: Double) {
        val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
            putExtra("orderId", orderId)
            putExtra("total", total)
            // Clear the cart + checkout from back stack
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }
}