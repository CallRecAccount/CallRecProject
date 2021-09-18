package uz.invan.rovitalk.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.invan.rovitalk.data.models.entities.BMEntity

@Dao
interface BMDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBM(bm: BMEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBMs(bms: List<BMEntity>)

    @Query("SELECT * FROM background_musics WHERE category = :category")
    suspend fun retrieveBMsByCategory(category: String): List<BMEntity>

    @Query("DELETE FROM background_musics WHERE category = :category")
    suspend fun clearBMsByCategory(category: String)

    @Query("DELETE FROM background_musics")
    suspend fun clearBMs()
}