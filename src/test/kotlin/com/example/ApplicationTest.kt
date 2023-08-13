package com.example

import com.example.model.ApiResponse
import com.example.model.Hero
import com.example.plugins.*
import com.example.repository.HeroRepositoryImpl
import com.example.repository.NEXT_PAGE_KEY
import com.example.repository.PREVIOUS_PAGE_KEY
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import kotlin.test.*
import org.koin.test.KoinTest

class ApplicationTest : KoinTest {

    //private val repository: HeroRepository by inject() // Does not work !!!

    @BeforeTest
    fun setUp() {
        testApplication {
            application {
                configureRouting()
            }
        }
    }

    @Test
    fun `access root endpoint, assert correct info`() = testApplication {
        client.get("/").apply {
            assertEquals(expected = HttpStatusCode.OK, actual = status)
            assertEquals(expected = "Welcome to Boruto API!", actual = bodyAsText())
        }
    }

    @OptIn(InternalAPI::class)
    @Test
    fun `access all heroes endpoint, assert correct info` () = testApplication {
        val repository = HeroRepositoryImpl() // Koin Injection does not work properly
        client.get("/boruto/heroes").apply {
            assertEquals(expected = HttpStatusCode.OK, actual = status)

            val expectedResponse = ApiResponse<Hero>(
                success = true,
                message = "Ok",
                previousPage = null,
                nextPage = 2,
                result = repository.page1
            )

            val actualResponse = Json.decodeFromString<ApiResponse<Hero>>(bodyAsText())
            assertEquals(expected = expectedResponse, actual = actualResponse)
        }
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct info` () {
        /*stopKoin() // to remove 'A Koin Application has already been started'
        startKoin {
            modules(koinTestModule)
        }*/
        testApplication {
            val repository = HeroRepositoryImpl() // Koin Injection does not work properly !!!
            val pages = 1..5
            val heroes = listOf(
                repository.page1,
                repository.page2,
                repository.page3,
                repository.page4,
                repository.page5
            )
            pages.forEach { page ->
                client.get("/boruto/heroes?page=$page").apply {
                    assertEquals(expected = HttpStatusCode.OK, actual = status)

                    val expectedResponse = ApiResponse(
                        success = true,
                        message = "Ok",
                        previousPage = calculatePage(page = page)[PREVIOUS_PAGE_KEY],
                        nextPage = calculatePage(page = page)[NEXT_PAGE_KEY],
                        result = heroes[page - 1]
                    )

                    val actualResponse = Json.decodeFromString<ApiResponse<Hero>>(bodyAsText())
                    assertEquals(expected = expectedResponse, actual = actualResponse)
                }
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query non existing page number, assert error` () = testApplication {
        client.get("/boruto/heroes?page=6").apply {
            assertEquals(expected = HttpStatusCode.NotFound, actual = status)
            assertEquals(expected = "Page not found.", actual = bodyAsText())
        }
    }

    @Test
    fun `access all heroes endpoint, query invalid page number, assert error` () = testApplication {
        client.get("/boruto/heroes?page=blabla").apply {
            assertEquals(expected = HttpStatusCode.BadRequest, actual = status)

            val expectedResponse = ApiResponse<Hero>(
                success = false,
                message = "Only numbers are permitted."
            )
            val actualResponse = Json.decodeFromString<ApiResponse<Hero>>(bodyAsText())
            assertEquals(expected = expectedResponse, actual = actualResponse)
        }
    }


    private fun calculatePage(page: Int): Map<String, Int?> {
        var previousPage: Int? = page
        var nextPage: Int? = page

        if (page == 1) previousPage = null
        if (page == 5) nextPage = null
        if (page in 1..4) nextPage = nextPage?.plus(1)
        if (page in 2..5) previousPage = previousPage?.minus(1)
        return mapOf(PREVIOUS_PAGE_KEY to previousPage, NEXT_PAGE_KEY to nextPage)
    }
}
