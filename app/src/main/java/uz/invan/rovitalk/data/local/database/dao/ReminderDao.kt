package uz.invan.rovitalk.data.local.database.dao

import androidx.room.*
import uz.invan.rovitalk.data.models.entities.ReminderEntity

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReminder(reminder: ReminderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<ReminderEntity>)

    @Query("SELECT * FROM reminders WHERE id = :id LIMIT 1")
    suspend fun retrieveReminder(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders")
    suspend fun retrieveReminders(): List<ReminderEntity>

    @Delete
    suspend fun removeReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE isPrivate = 0")
    suspend fun clearPublicReminders()

    @Query("DELETE FROM reminders")
    suspend fun clearReminders()
}