package com.example.data.repository

import com.example.data.local.dao.FavoriteDao
import com.example.data.local.dao.HistoryDao
import com.example.data.local.entity.CalculationHistory
import com.example.data.local.entity.CalculationType
import com.example.data.local.entity.FavoriteCalculation
import kotlinx.coroutines.flow.Flow

class CalculatorRepository(
    private val historyDao: HistoryDao,
    private val favoriteDao: FavoriteDao
) {
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()
    val allFavorites: Flow<List<FavoriteCalculation>> = favoriteDao.getAllFavorites()

    suspend fun addHistory(
        expression: String,
        result: String,
        type: CalculationType = CalculationType.BASIC
    ): Long {
        if (expression.isBlank() || result.isBlank()) return -1L
        return historyDao.insert(
            CalculationHistory(
                expression = expression.trim(),
                result = result.trim(),
                calculationType = type
            )
        )
    }

    suspend fun deleteHistory(history: CalculationHistory) {
        historyDao.delete(history)
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteById(id)
    }

    suspend fun clearHistory() {
        historyDao.clearAll()
    }

    suspend fun addFavorite(
        title: String,
        expression: String,
        result: String,
        note: String = "",
        type: CalculationType = CalculationType.BASIC
    ): Long {
        return favoriteDao.insert(
            FavoriteCalculation(
                title = title.ifBlank { expression },
                expression = expression,
                result = result,
                note = note,
                calculationType = type
            )
        )
    }

    suspend fun updateFavorite(favorite: FavoriteCalculation) {
        favoriteDao.update(favorite)
    }

    suspend fun deleteFavorite(favorite: FavoriteCalculation) {
        favoriteDao.delete(favorite)
        historyDao.setFavorite(favorite.id, false)
    }

    suspend fun deleteFavoriteById(id: Long) {
        favoriteDao.deleteById(id)
    }
}
