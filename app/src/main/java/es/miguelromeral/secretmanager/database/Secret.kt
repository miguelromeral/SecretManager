package es.miguelromeral.secretmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Secret.TABLE_NAME)
data class Secret (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "time")
    val time: Long = System.currentTimeMillis(),


    @ColumnInfo(name = "alias")
    var alias: String = String(),

    @ColumnInfo(name = "content")
    var content: String = String()

){


    companion object {
        const val TABLE_NAME = "secret"
    }
}