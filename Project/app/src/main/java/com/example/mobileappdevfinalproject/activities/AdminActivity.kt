package com.example.mobileappdevfinalproject.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappdevfinalproject.Adapter.AdminOrderAdapter
import com.example.mobileappdevfinalproject.Adapter.AdminProductAdapter
import com.example.mobileappdevfinalproject.databinding.ActivityAdminBinding
import com.example.mobileappdevfinalproject.model.ItemModel
import com.example.mobileappdevfinalproject.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val db   = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    private val orderList   = mutableListOf<OrderModel>()
    private val productList = mutableListOf<ItemModel>()

    private lateinit var orderAdapter: AdminOrderAdapter
    private lateinit var productAdapter: AdminProductAdapter

    private var ordersListener: ValueEventListener?   = null
    private var productsListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ── Guard: only admin email gets in ──────────────────────────────
        val currentEmail = auth.currentUser?.email ?: ""
        if (!currentEmail.equals(LoginActivity.ADMIN_EMAIL, ignoreCase = true)) {
            Toast.makeText(this, "Access denied", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupAdapters()
        setupTabs()
        loadOrders()
        setupAddProductBtn()

        // Admin logout button
        binding.btnAdminLogout.setOnClickListener { confirmLogout() }
    }

    // ─── Adapters ─────────────────────────────────────────────────────────

    private fun setupAdapters() {
        orderAdapter = AdminOrderAdapter(orderList) { order, newStatus ->
            updateOrderStatus(order, newStatus)
        }
        productAdapter = AdminProductAdapter(productList,
            onEdit   = { item -> showEditProductDialog(item) },
            onDelete = { item -> deleteProduct(item) }
        )
        binding.rvOrders.layoutManager   = LinearLayoutManager(this)
        binding.rvOrders.adapter         = orderAdapter
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
        binding.rvProducts.adapter       = productAdapter
    }

    // ─── Tab switching ────────────────────────────────────────────────────

    private fun setupTabs() {
        binding.btnTabOrders.setOnClickListener   { showOrdersTab() }
        binding.btnTabProducts.setOnClickListener {
            showProductsTab()
            loadProducts()
        }
    }

    private fun showOrdersTab() {
        binding.layoutOrders.visibility   = View.VISIBLE
        binding.layoutProducts.visibility = View.GONE
        binding.btnTabOrders.alpha   = 1f
        binding.btnTabProducts.alpha = 0.5f
    }

    private fun showProductsTab() {
        binding.layoutOrders.visibility   = View.GONE
        binding.layoutProducts.visibility = View.VISIBLE
        binding.btnTabOrders.alpha   = 0.5f
        binding.btnTabProducts.alpha = 1f
    }

    // ─── Orders ──────────────────────────────────────────────────────────

    private fun loadOrders() {
        binding.progressOrders.visibility = View.VISIBLE
        val ref = db.child("orders").orderByChild("timestamp")
        ordersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (child in snapshot.children) {
                    child.getValue(OrderModel::class.java)?.let { orderList.add(0, it) }
                }
                orderAdapter.notifyDataSetChanged()
                binding.progressOrders.visibility = View.GONE
                binding.tvNoOrders.visibility =
                    if (orderList.isEmpty()) View.VISIBLE else View.GONE

                val pending = orderList.count { it.status == "Pending" }
                binding.tvPendingBadge.text       = if (pending > 0) "$pending new" else ""
                binding.tvPendingBadge.visibility = if (pending > 0) View.VISIBLE else View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressOrders.visibility = View.GONE
                Toast.makeText(this@AdminActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
        ref.addValueEventListener(ordersListener!!)
    }

    private fun updateOrderStatus(order: OrderModel, newStatus: String) {
        db.child("orders").child(order.orderId).child("status").setValue(newStatus)
            .addOnSuccessListener {
                db.child("admin_notifications").child(order.orderId)
                    .child("status").setValue(newStatus)
                Toast.makeText(this, "Status → $newStatus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    // ─── Products ─────────────────────────────────────────────────────────

    private fun loadProducts() {
        binding.progressProducts.visibility = View.VISIBLE
        val ref = db.child("Items")
        productsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (child in snapshot.children) {
                    child.getValue(ItemModel::class.java)?.let {
                        it.key = child.key ?: ""
                        productList.add(it)
                    }
                }
                productAdapter.notifyDataSetChanged()
                binding.progressProducts.visibility = View.GONE
                binding.tvNoProducts.visibility =
                    if (productList.isEmpty()) View.VISIBLE else View.GONE
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressProducts.visibility = View.GONE
            }
        }
        ref.addValueEventListener(productsListener!!)
    }

    private fun setupAddProductBtn() {
        binding.btnAddProduct.setOnClickListener { showAddProductDialog() }
    }

    private fun showAddProductDialog() {
        val dialogView = layoutInflater.inflate(
            com.example.mobileappdevfinalproject.R.layout.dialog_product_form, null
        )
        AlertDialog.Builder(this)
            .setTitle("Add New Product")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title       = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductTitle)
                val price       = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPrice).toDoubleOrNull() ?: 0.0
                val oldPrice    = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductOldPrice).toDoubleOrNull() ?: 0.0
                val description = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductDescription)
                val picUrl      = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPicUrl)

                if (title.isEmpty()) {
                    Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val newItem = ItemModel(
                    title       = title,
                    price       = price,
                    oldPrice    = oldPrice,
                    description = description,
                    picUrl      = if (picUrl.isNotEmpty()) arrayListOf(picUrl) else arrayListOf()
                )
                db.child("Items").push().setValue(newItem)
                    .addOnSuccessListener { Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditProductDialog(item: ItemModel) {
        val dialogView = layoutInflater.inflate(
            com.example.mobileappdevfinalproject.R.layout.dialog_product_form, null
        )
        setField(dialogView, com.example.mobileappdevfinalproject.R.id.etProductTitle,       item.title).isEnabled = false
        setField(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPrice,       item.price.toString())
        setField(dialogView, com.example.mobileappdevfinalproject.R.id.etProductOldPrice,    item.oldPrice.toString())
        setField(dialogView, com.example.mobileappdevfinalproject.R.id.etProductDescription, item.description)
        setField(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPicUrl,      item.picUrl.firstOrNull() ?: "")

        AlertDialog.Builder(this)
            .setTitle("Edit Product")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val price       = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPrice).toDoubleOrNull() ?: item.price
                val oldPrice    = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductOldPrice).toDoubleOrNull() ?: item.oldPrice
                val description = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductDescription)
                val picUrl      = field(dialogView, com.example.mobileappdevfinalproject.R.id.etProductPicUrl)

                val updates = hashMapOf<String, Any>(
                    "price"       to price,
                    "oldPrice"    to oldPrice,
                    "description" to description
                )
                if (picUrl.isNotEmpty()) {
                    updates["picUrl"] = arrayListOf(picUrl)
                }

                if (item.key.isEmpty()) {
                    Toast.makeText(this, "Error: Product key missing", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                db.child("Items").child(item.key).updateChildren(updates)
                    .addOnSuccessListener { Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show() }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteProduct(item: ItemModel) {
        if (item.key.isEmpty()) {
            Toast.makeText(this, "Error: Product key missing", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete ${item.title}?")
            .setPositiveButton("Delete") { _, _ ->
                db.child("Items").child(item.key).removeValue()
                    .addOnSuccessListener { Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show() }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ─── Logout ──────────────────────────────────────────────────────────

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Log Out")
            .setMessage("Log out of admin panel?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onBackPressed() {
        confirmLogout()
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private fun field(view: android.view.View, id: Int): String =
        view.findViewById<android.widget.EditText>(id).text.toString().trim()

    private fun setField(view: android.view.View, id: Int, value: String): android.widget.EditText =
        view.findViewById<android.widget.EditText>(id).also { it.setText(value) }

    // ─── Lifecycle ───────────────────────────────────────────────────────

    override fun onDestroy() {
        super.onDestroy()
        ordersListener?.let  { db.child("orders").removeEventListener(it) }
        productsListener?.let { db.child("Items").removeEventListener(it) }
    }
}