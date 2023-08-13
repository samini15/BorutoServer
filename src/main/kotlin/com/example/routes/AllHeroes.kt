package com.example.routes

import com.example.model.ApiResponse
import com.example.model.Hero
import com.example.repository.HeroRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

fun Route.getAllHeroes() {

    val repository: HeroRepository by inject()

    get("/boruto/heroes") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            require(page in 1..5)

            val apiResponse = repository.getAllHeroes(page = page)

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
}