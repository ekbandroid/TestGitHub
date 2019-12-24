package com.testgithub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testgithub.db.TestGitHubDatabase.Companion.DATABASE_VERSION

@Database(
    entities = [FavoriteRepositoryEntity::class],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class TestGitHubDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 1
    }

    abstract fun favoriteRepositoriesDao(): FavoriteRepositoriesDao
}