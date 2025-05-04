package com.example.yadro_test.di

import android.content.Context
import com.example.yadro_test.data.ContactDuplicateServiceConnector
import com.example.yadro_test.data.ContactsAppRepositoryImpl
import com.example.yadro_test.domain.ContactsAppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideContactsAppRepository(
        @ApplicationContext context: Context,
        connector: ContactDuplicateServiceConnector
    ): ContactsAppRepository {
        return ContactsAppRepositoryImpl(context.contentResolver, connector)
    }
}