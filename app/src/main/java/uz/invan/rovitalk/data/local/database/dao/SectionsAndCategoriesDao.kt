package uz.invan.rovitalk.data.local.database.dao

import androidx.room.*
import uz.invan.rovitalk.data.models.entities.CategoryEntity
import uz.invan.rovitalk.data.models.entities.SectionEntity
import uz.invan.rovitalk.data.models.entities.SubCategoryEntity

@Dao
interface SectionsAndCategoriesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSection(section: SectionEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSection(section: SectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSections(sections: List<SectionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCategory(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategory(subCategory: SubCategoryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSubCategory(subCategory: SubCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubCategories(subCategories: List<SubCategoryEntity>)

    @Query("SELECT * FROM sections WHERE id = :section LIMIT 1")
    suspend fun retrieveSection(section: String): SectionEntity?

    @Query("SELECT * FROM sections")
    suspend fun retrieveSections(): List<SectionEntity>

    @Query("SELECT * FROM categories WHERE id = :category LIMIT 1")
    suspend fun retrieveCategory(category: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE section = :section")
    suspend fun retrieveCategoriesBySection(section: String): List<CategoryEntity>

    @Query("SELECT * FROM sub_categories WHERE id = :subCategory LIMIT 1")
    suspend fun retrieveSubCategory(subCategory: String): SubCategoryEntity?

    // TODO: clear
    @Query("SELECT * FROM sub_categories WHERE category = :category")
    suspend fun retrieveSubCategoriesByCategory(category: String): List<SubCategoryEntity>

    @Query("DELETE FROM sections")
    suspend fun clearSections()

    @Query("DELETE FROM categories WHERE section = :section")
    suspend fun clearCategoriesBySection(section: String)

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM sub_categories WHERE category = :category")
    suspend fun clearSubCategoriesByCategory(category: String)

    @Query("DELETE FROM sub_categories")
    suspend fun clearSubCategories()
}