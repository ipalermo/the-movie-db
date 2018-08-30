
package com.android.example.themoviedb.di

import android.app.Application
import android.arch.persistence.room.Room
import com.android.example.themoviedb.api.TmdbService
import com.android.example.themoviedb.api.TmdbService.Companion.BASE_URL
import com.android.example.themoviedb.db.MovieDao
import com.android.example.themoviedb.db.MoviesDb
import com.android.example.themoviedb.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {
    @Singleton
    @Provides
    fun provideTmdbService(): TmdbService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(TmdbService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): MoviesDb {
        return Room
            .databaseBuilder(app, MoviesDb::class.java, "themovie.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideMovieDao(db: MoviesDb): MovieDao {
        return db.movieDao()
    }
}
