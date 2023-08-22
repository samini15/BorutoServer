package com.example.repository

import com.example.model.ApiResponse
import com.example.model.Hero

interface HeroRepositoryAlternative {

    val heroes: List<Hero>

    suspend fun getAllHeroes(): ApiResponse<Hero>

    suspend fun getAllHeroes(page: Int = 1, limit: Int = 4): ApiResponse<Hero>

    suspend fun searchHeroes(name: String?): ApiResponse<Hero>
}