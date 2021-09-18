package uz.invan.rovitalk.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.invan.rovitalk.data.local.database.dao.*
import uz.invan.rovitalk.data.models.entities.*

@Database(
    entities = [SectionEntity::class, CategoryEntity::class, SubCategoryEntity::class, BMEntity::class, AudioEntity::class, PriceEntity::class, ReminderEntity::class],
    version = 1
)
abstract class RoviDatabase : RoomDatabase() {
    abstract fun sectionsAndCategoriesDao(): SectionsAndCategoriesDao
    abstract fun bmDao(): BMDao
    abstract fun audioDao(): AudioDao
    abstract fun priceDao(): PriceDao
    abstract fun reminderDao(): ReminderDao
}