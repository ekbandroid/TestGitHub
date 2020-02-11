package com.testgithub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testgithub.db.TestGitHubDatabase.Companion.DATABASE_VERSION
import com.testgithub.repositories.favorites.db.FavoriteRepositoriesDao
import com.testgithub.repositories.favorites.db.FavoriteRepositoryEntity
import com.testgithub.repositories.search.db.SearchedRepositoriesDao
import com.testgithub.repositories.search.db.SearchedRepositoryEntity

@Database(
    entities = [
        FavoriteRepositoryEntity::class,
        SearchedRepositoryEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class TestGitHubDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 2
    }

    abstract fun favoriteRepositoriesDao(): FavoriteRepositoriesDao

    abstract fun searchedRepositoriesDao(): SearchedRepositoriesDao
}