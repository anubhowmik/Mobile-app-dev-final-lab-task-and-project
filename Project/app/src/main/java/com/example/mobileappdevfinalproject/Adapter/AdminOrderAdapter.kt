package com.example.mobileappdevfinalproject.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevfinalproject.R
import com.example.mobileappdevfinalproject.model.OrderModel
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderAdapter(
    private val orders: List<OrderModel>,
    private val onStatusChange: (OrderModel, String) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder>() {

    private val statusOptions = listOf("Pending", "Confirmed", "Shipped", "Delivered", "Cancelled")
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderId: TextView = view.findViewById(R.id.tvAdminOrderId)
        val tvCustomer: TextView = view.findViewById(R.id.tvAdminCustomer)
        val tvAddress: TextView = view.findViewById(R.id.tvAdminAddress)
        val tvItems: TextView = view.findViewById(R.id.tvAdminItems)
        val tvTotal: TextView = view.findViewById(R.id.tvAdminTotal)
        val tvDate: TextView = view.findViewById(R.id.tvAdminDate)
        val tvPayment: TextView = view.findViewById(R.id.tvAdminPayment)
        val spinnerStatus: Spinner = view.findViewById(R.id.spinnerOrderStatus)
        val tvStatusBadge: TextView = view.findViewById(R.id.tvStatusBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_admin_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        val ctx = holder.itemView.context

        holder.tvOrderId.text = "Order: #${order.orderId.take(8).uppercase()}"
        holder.tvCustomer.text = "${order.fullName}  •  ${order.phone}\n${order.userEmail}"
        holder.tvAddress.text = "${order.address}, ${order.city} - ${order.postalCode}"
        holder.tvItems.text = order.items.joinToString("\n") {
            "• ${it.title} × ${it.quantity}  @  $${it.price}"
        }
        holder.tvTotal.text = "Total: $${"%.2f".format(order.total)}"
        holder.tvDate.text = dateFormat.format(Date(order.timestamp))
        holder.tvPayment.text = "Payment: ${order.paymentMethod}"

        // Status badge color
        holder.tvStatusBadge.text = order.status
        holder.tvStatusBadge.setTextColor(Color.WHITE)
        holder.tvStatusBadge.setBackgroundColor(
            when (order.status) {
                "Pending" -> Color.parseColor("#FF9800")
                "Confirmed" -> Color.parseColor("#2196F3")
                "Shipped" -> Color.parseColor("#9C27B0")
                "Delivered" -> Color.parseColor("#4CAF50")
                "Cancelled" -> Color.parseColor("#F44336")
                else -> Color.GRAY
            }
        )

        // Spinner
        val adapter = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerStatus.adapter = adapter
        holder.spinnerStatus.setSelection(statusOptions.indexOf(order.status).coerceAtLeast(0))

        holder.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val selected = statusOptions[pos]
                if (selected != order.status) {
                    onStatusChange(order, selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun getItemCount() = orders.size
}