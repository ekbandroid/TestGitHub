package com.testgithub.repositories.search.db

import androidx.paging.DataSource
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface SearchRepositoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(posts: List<SearchRepositoryEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(post: SearchRepositoryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(repository: SearchRepositoryEntity): Completable

    @Query(
        """
        SELECT * FROM ${SearchRepositoryEntity.TABLE_NAME} WHERE 
        (${SearchRepositoryEntity.COLUMN_SEARCH_TEXT} LIKE :queryString) OR
         (${SearchRepositoryEntity.COLUMN_SEARCH_TEXT} = '')
    """
    )
    fun reposByName(queryString: String): DataSource.Factory<Int, SearchRepositoryEntity>

    @Query("DELETE FROM ${SearchRepositoryEntity.TABLE_NAME}")
    fun deleteAll()

    @Query(
        """
        SELECT * FROM ${SearchRepositoryEntity.TABLE_NAME} 
        WHERE ${SearchRepositoryEntity.COLUMN_ID}=:id
        """
    )
    fun getSearchedRepositoryById(id: String): Maybe<SearchRepositoryEntity>

    @Query(
        """
        DELETE FROM ${SearchRepositoryEntity.TABLE_NAME} WHERE 
        (${SearchRepositoryEntity.COLUMN_SEARCH_TEXT} = '')
        """
    )
    fun deleteEmpty(): Completable
}