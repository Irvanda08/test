package com.example.test.di

import android.content.Context
import com.example.test.data.UserRepository
import com.example.test.data.pref.UserPreference
import com.example.test.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}