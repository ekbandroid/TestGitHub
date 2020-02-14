package com.testgithub.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.testgithub.db.TestGitHubDatabase.Companion.DATABASE_VERSION
import com.testgithub.repositories.favorite.db.FavoriteRepositoriesDao
import com.testgithub.repositories.favorite.db.FavoriteRepositoryEntity
import com.testgithub.repositories.search.db.SearchRepositoriesDao
import com.testgithub.repositories.search.db.SearchRepositoryEntity

@Database(
    entities = [
        FavoriteRepositoryEntity::class,
        SearchRepositoryEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class TestGitHubDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_VERSION = 2
    }

    abstract fun favoriteRepositoriesDao(): FavoriteRepositoriesDao

    abstract fun searchedRepositoriesDao(): SearchRepositoriesDao
}