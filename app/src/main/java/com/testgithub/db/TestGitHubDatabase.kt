package com.testgithub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testgithub.db.TestGitHubDatabase.Companion.DATABASE_VERSION

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