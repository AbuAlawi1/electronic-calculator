package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.CalculationHistory
import com.example.data.local.entity.FavoriteCalculation
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM calculation_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: CalculationHistory): Long

    @Delete
    suspend fun delete(history: CalculationHistory)

    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()

    @Query("UPDATE calculation_history SET isFavorite = :isFav WHERE id = :id")
    suspend fun setFavorite(id: Long, isFav: Boolean)
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_calculations ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteCalculation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: FavoriteCalculation): Long

    @Update
    suspend fun update(favorite: FavoriteCalculation)

    @Delete
    suspend fun delete(favorite: FavoriteCalculation)

    @Query("DELETE FROM favorite_calculations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM favorite_calculations WHERE expression = :expression AND result = :result")
    suspend fun deleteByExpressionAndResult(expression: String, result: String)
}
