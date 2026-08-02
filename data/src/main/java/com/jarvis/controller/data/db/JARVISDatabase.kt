package com.jarvis.controller.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jarvis.controller.data.db.dao.MessageDao
import com.jarvis.controller.data.db.entity.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class JARVISDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}
