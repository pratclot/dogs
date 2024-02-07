package com.pratclot.dogs.di

import android.content.Context
import androidx.room.Room
import com.pratclot.dogs.BuildConfig
import com.pratclot.dogs.data.db.LikeDb
import com.pratclot.dogs.data.db.LikeDbDao
import com.pratclot.dogs.service.DogApi
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://dog.ceo"

@Module
@InstallIn(SingletonComponent::class)
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
        return OkHttpClient.Builder()
            .apply {
                val interceptor = HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
                if (BuildConfig.DEBUG) addInterceptor(interceptor)
            }
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