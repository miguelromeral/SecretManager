package es.miguelromeral.secretmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface SecretDatabaseDao {

    @Insert
    fun insert(secret: Secret)

   /* @Query("SELECT * FROM secret WHERE alias LIKE :criteria")
    fun search(criteria: String)
*/
    @Query("DELETE FROM ${Secret.TABLE_NAME} WHERE id = :key")
    fun deleteFromKey(key: Long)

    @Query("SELECT * FROM ${Secret.TABLE_NAME} ORDER BY id DESC")
    fun getAllSecrets(): LiveData<List<Secret>>

    @Query("DELETE FROM ${Secret.TABLE_NAME}")
    fun clearStarts()


/*
    @RawQuery
    fun insertDataRawFormat(SupportSQLiteQuery query): Boolean */
}