package es.miguelromeral.secretmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Secret::class], version = 1, exportSchema = false)
abstract class SecretDatabase : RoomDatabase() {

    abstract val secretDatabaseDao: SecretDatabaseDao

    companion object{
        @Volatile
        private var INSTANCE: SecretDatabase? = null

        fun getInstance(context: Context): SecretDatabase {
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SecretDatabase::class.java,
                        "secret_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}