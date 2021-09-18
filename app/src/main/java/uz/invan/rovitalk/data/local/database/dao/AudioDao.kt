package uz.invan.rovitalk.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.invan.rovitalk.data.models.entities.AudioEntity

@Dao
interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudio(audio: AudioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAudios(audios: List<AudioEntity>)

    @Query("SELECT * FROM audios WHERE category = :category")
    suspend fun retrieveAudiosByCategory(category: String): List<AudioEntity>

    @Query("DELETE FROM audios WHERE category = :category")
    suspend fun clearAudiosByCategory(category: String)

    @Query("DELETE FROM audios")
    suspend fun clearAudios()
}