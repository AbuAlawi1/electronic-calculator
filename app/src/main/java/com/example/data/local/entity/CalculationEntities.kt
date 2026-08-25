package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CalculationType {
    BASIC,
    SCIENTIFIC,
    FINANCIAL,
    CONVERTER,
    MATH
}

@Entity(tableName = "calculation_history")
data class CalculationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val calculationType: CalculationType = CalculationType.BASIC,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(tableName = "favorite_calculations")
data class FavoriteCalculation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val expression: String,
    val result: String,
    val note: String = "",
    val calculationType: CalculationType = CalculationType.BASIC,
    val timestamp: Long = System.currentTimeMillis()
)
