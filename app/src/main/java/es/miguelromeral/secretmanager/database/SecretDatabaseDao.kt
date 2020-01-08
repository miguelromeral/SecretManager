package es.miguelromeral.secretmanager.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SecretDatabaseDao {

    @Insert
    fun insert(secret: Secret)

   /* @Query("SELECT * FROM secret WHERE alias LIKE :criteria")
    fun search(criteria: String)
*/
    @Query("DELETE FROM secret WHERE id = :key")
    fun deleteFromKey(key: Long)

    @Query("SELECT * FROM secret ORDER BY id DESC")
    fun getAllSecrets(): LiveData<List<Secret>>
}