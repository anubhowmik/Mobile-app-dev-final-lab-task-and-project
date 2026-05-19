package com.example.mobileappdevfinalproject.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevfinalproject.R
import com.example.mobileappdevfinalproject.model.ItemModel

class AdminProductAdapter(
    private val products: List<ItemModel>,
    private val onEdit: (ItemModel) -> Unit,
    private val onDelete: (ItemModel) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvProductTitle)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvOldPrice: TextView = view.findViewById(R.id.tvProductOldPrice)
        val tvDesc: TextView = view.findViewById(R.id.tvProductDesc)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditProduct)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_admin_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = products[position]

        holder.tvTitle.text = item.title
        holder.tvPrice.text = "Price: $${item.price}"
        holder.tvOldPrice.text = "Old Price: $${item.oldPrice}"
        holder.tvDesc.text = item.description.take(80) + if (item.description.length > 80) "…" else ""

        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = products.size
}
