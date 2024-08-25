package com.mbp16.shsdishwiget.utils

import androidx.room.*

@Entity
data class MealData(
    @PrimaryKey val dateType: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "meal") val meal: String,
    @ColumnInfo(name = "calorie") val calorie: String
)

@Dao
interface MealDataDao {
    @Query("SELECT * FROM MealData")
    fun getAll(): List<MealData>

    @Query("SELECT COUNT(*) FROM MealData")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg mealData: MealData)

    @Query("DELETE FROM MealData WHERE dateType IN (:dateType)")
    fun delete(dateType: List<String>)

    @Query("DELETE FROM MealData")
    fun deleteAll()
}

@Database(entities = [MealData::class], version = 1, exportSchema = false)
abstract class MealDatabase : RoomDatabase() {
    abstract fun mealDataDao(): MealDataDao
}