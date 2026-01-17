package com.example.encryptedrecords

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object DatabaseProvider {
    private const val DATABASE_NAME = "records.db"
    private var instance: AppDatabase? = null
    private var currentPassphrase: String? = null

    fun getDatabase(context: Context, passphrase: String): AppDatabase {
        val appContext = context.applicationContext
        if (instance == null || currentPassphrase != passphrase) {
            instance?.close()
            val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
            instance = Room.databaseBuilder(appContext, AppDatabase::class.java, DATABASE_NAME)
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration()
                .build()
            currentPassphrase = passphrase
        }
        return instance!!
    }

    fun getDatabaseFile(context: Context): java.io.File {
        return context.getDatabasePath(DATABASE_NAME)
    }
}
