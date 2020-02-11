package com.testgithub.repositories.search.db

import androidx.paging.DataSource
import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface SearchedRepositoriesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(posts: List<SearchedRepositoryEntity>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(post: SearchedRepositoryEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(repository: SearchedRepositoryEntity): Completable

    @Query(
        """
        SELECT * FROM ${SearchedRepositoryEntity.TABLE_NAME} WHERE 
        (${SearchedRepositoryEntity.COLUMN_SEARCH_TEXT} LIKE :queryString) OR
         (${SearchedRepositoryEntity.COLUMN_SEARCH_TEXT} = '')
    """
    )
    fun reposByName(queryString: String): DataSource.Factory<Int, SearchedRepositoryEntity>

    @Query("DELETE FROM ${SearchedRepositoryEntity.TABLE_NAME}")
    fun deleteAll()

    @Query(
        """
        SELECT * FROM ${SearchedRepositoryEntity.TABLE_NAME} 
        WHERE ${SearchedRepositoryEntity.COLUMN_ID}=:id
        """
    )
    fun getSearchedRepositoryById(id: String): Maybe<SearchedRepositoryEntity>

    @Query(
        """
        DELETE FROM ${SearchedRepositoryEntity.TABLE_NAME} WHERE 
        (${SearchedRepositoryEntity.COLUMN_SEARCH_TEXT} = '')
        """
    )
    fun deleteEmpty(): Completable
}