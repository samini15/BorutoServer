package com.example.routes

import com.example.model.ApiResponse
import com.example.model.Hero
import com.example.repository.HeroRepository
import com.example.repository.HeroRepositoryAlternative
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun Route.getAllHeroesAlternative() {

    val repository: HeroRepositoryAlternative by inject()

    get("/boruto/heroes") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            val limit = call.request.queryParameters["limit"]?.toInt() ?: 4

            val apiResponse = repository.getAllHeroes(page = page, limit = limit)

            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )
        } catch (e: Exception) {
            when(e) {
                is NumberFormatException ->
                    call.respond(
                        message = ApiResponse<Hero>(success = false, message = "Only numbers are permitted."),
                        status = HttpStatusCode.BadRequest
                    )
                is IllegalArgumentException ->
                    call.respond(
                        message = ApiResponse<Hero>(success = false, message = "Heroes not found"),
                        status = HttpStatusCode.NotFound
                    )
            }
        }

    }

    get("/boruto/heroes/all") {
        try {
            val apiResponse = repository.getAllHeroes()

            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )
        } catch (e: Exception) {
            call.respond(
                message = ApiResponse<Hero>(success = false, message = e.message),
                status = HttpStatusCode.BadRequest
            )
        }

    }
}