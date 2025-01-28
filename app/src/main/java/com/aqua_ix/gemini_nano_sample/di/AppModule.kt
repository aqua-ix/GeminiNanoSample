package com.aqua_ix.gemini_nano_sample.di

import android.content.Context
import com.google.ai.edge.aicore.GenerativeModel
import com.google.ai.edge.aicore.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGenerativeModel(
        @ApplicationContext appContext: Context
    ): GenerativeModel {
        return GenerativeModel(
            generationConfig = generationConfig {
                context = appContext
                temperature = 0.2f
                topK = 16
                maxOutputTokens = 256
            }
        )
    }
}
