package com.pratclot.dogs.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pratclot.dogs.data.db.LikeDb
import com.pratclot.dogs.data.db.LikeDbDao
import com.pratclot.dogs.service.DogApi
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.temporal.Temporal
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BASE_URL = "https://dog.ceo"

@Module
@InstallIn(ApplicationComponent::class)
class Module1 {
    @Singleton
    @Provides
    fun provideRetrofitClient(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(provideOkHttpClient())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
//            .callTimeout(30L, TimeUnit.MILLISECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitService(retrofit: Retrofit): DogApi {
        return retrofit.create(DogApi::class.java)
    }

    @Singleton
    @Provides
    fun providePicasso(@ApplicationContext context: Context): Picasso {
        return Picasso.Builder(context)
            .downloader(OkHttp3Downloader(provideOkHttpClient()))
            .build()
    }

    @Singleton
    @Provides
    fun provideLikeDb(@ApplicationContext context: Context): LikeDb {
        return Room.databaseBuilder(
            context,
            LikeDb::class.java,
            "likedb"
        ).build()
    }

    @Singleton
    @Provides
    fun provideDao(db: LikeDb): LikeDbDao {
        return db.getDao()
    }
}