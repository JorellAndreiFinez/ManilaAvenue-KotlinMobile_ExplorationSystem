package com.example.manilaavenue.model

data class LocationModel(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val description: String,
    val category: String
) {
    // No need to explicitly declare a constructor if it matches the data class properties
    // If you need custom initialization logic, you can add it here
}
