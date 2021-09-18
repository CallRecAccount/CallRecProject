package uz.invan.rovitalk.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.invan.rovitalk.data.models.entities.PriceEntity

@Dao
interface PriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: PriceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<PriceEntity>)

    @Query("SELECT * FROM prices WHERE section = :section ORDER BY duration")
    suspend fun retrievePricesBySection(section: String): List<PriceEntity>

    @Query("DELETE FROM prices WHERE section = :section")
    suspend fun clearPricesBySection(section: String)

    @Query("DELETE FROM prices")
    suspend fun clearPrices()
}