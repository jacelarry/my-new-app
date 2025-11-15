package com.example.smsautomationapp

object ProductRepository {
    private val products = listOf(
        Product(option = "1", name = "250MB", price = 20)
        // Add more products here as needed
    )

    fun findProductByOption(option: String): Product? {
        return products.find { it.option == option }
    }
}
