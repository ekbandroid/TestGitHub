package com.testgithub.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testgithub.common.TestGitHubDatabase.Companion.DATABASE_VERSION
import com.testgithub.db.FavoriteRepositoriesDao
import com.testgithub.db.FavoriteRepositoryEntity

@Suppress("TooManyFunctions")
@Database(
    entities = [
        FavoriteRepositoryEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)
//@TypeConverters(Converters::class)
abstract class TestGitHubDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
    }

    abstract fun favoriteRepositoriesDao(): FavoriteRepositoriesDao

}