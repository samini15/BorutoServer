package com.example.dependencyInjection

import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryImpl
import org.koin.dsl.module

val koinModule = module {
    single<HeroRepository> {
        HeroRepositoryImpl()
    }
}