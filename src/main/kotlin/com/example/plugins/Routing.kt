package com.example.plugins

import com.example.routes.getAllHeroes
import com.example.routes.getAllHeroesAlternative
import com.example.routes.root
import com.example.routes.searchHeroes
import io.ktor.server.application.Application
import io.ktor.server.http.content.*
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        root()

        //getAllHeroes()
        getAllHeroesAlternative()

        searchHeroes()

        staticResources("/images", "images")
    }
}
