package com.example.mobileappdevfinalproject.model

import java.io.Serializable

data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val userEmail: String = "",

    // Personal / delivery details
    val fullName: String = "",
    val phone: String = "",
    val address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val paymentMethod: String = "Cash on Delivery",

    // Order content
    val items: List<OrderItemModel> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val delivery: Double = 10.0,
    val total: Double = 0.0,

    // Status: "Pending", "Confirmed", "Shipped", "Delivered", "Cancelled"
    val status: String = "Pending",
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class OrderItemModel(
    val title: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val picUrl: String = ""
) : Serializable