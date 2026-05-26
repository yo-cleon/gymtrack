package com.example.gymtrack.di

import android.content.Context
import androidx.room.Room
import com.example.gymtrack.data.AppDatabase
import com.example.gymtrack.data.GymRepository
import com.example.gymtrack.data.dao.EjercicioDao
import com.example.gymtrack.data.dao.EntrenamientoDao
import com.example.gymtrack.data.dao.RutinaDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gym_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideEjercicioDao(database: AppDatabase): EjercicioDao {
        return database.ejercicioDao()
    }

    @Provides
    fun provideRutinaDao(database: AppDatabase): RutinaDao {
        return database.rutinaDao()
    }

    @Provides
    fun provideEntrenamientoDao(database: AppDatabase): EntrenamientoDao {
        return database.entrenamientoDao()
    }

    @Provides
    @Singleton
    fun provideRepository(
        ejercicioDao: EjercicioDao,
        rutinaDao: RutinaDao,
        entrenamientoDao: EntrenamientoDao
    ): GymRepository {
        return GymRepository(ejercicioDao, rutinaDao, entrenamientoDao)
    }
}
