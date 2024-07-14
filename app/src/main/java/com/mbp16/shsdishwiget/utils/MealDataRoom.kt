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
    @Query("SELECT * FROM MealData WHERE dateType IN (:dates)")
    fun getByDateTypes(dates: List<String>): List<MealData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg mealData: MealData)
}

@Database(entities = [MealData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDataDao(): MealDataDao
}