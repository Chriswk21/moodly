package com.moodly.app.di

import android.content.Context
import androidx.room.Room
import com.moodly.app.data.local.MoodlyDatabase
import com.moodly.app.data.local.dao.MoodDao
import com.moodly.app.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): MoodlyDatabase {
        return Room.databaseBuilder(
            context,
            MoodlyDatabase::class.java,
            MoodlyDatabase.DATABASE_NAME
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideMoodDao(database: MoodlyDatabase): MoodDao {
        return database.moodDao()
    }
    
    @Provides
    @Singleton
    fun provideTaskDao(database: MoodlyDatabase): TaskDao {
        return database.taskDao()
    }
}
