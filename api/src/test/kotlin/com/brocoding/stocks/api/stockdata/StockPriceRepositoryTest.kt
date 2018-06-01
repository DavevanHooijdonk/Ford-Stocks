package com.brocoding.stocks.api.stockdata

import org.bson.Document
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Query
import java.time.LocalDate.MAX
import java.time.LocalDate.MIN

/**
 * Created by Dave van Hooijdonk on 31-5-2018.
 */
@RunWith(MockitoJUnitRunner::class)
class StockPriceRepositoryTest {

    @Mock
    private lateinit var mongoOperations: MongoOperations

    @Captor
    private lateinit var queryCaptor: ArgumentCaptor<Query>

    private lateinit var stockPriceRepository: StockPriceAdvancedRepository

    @Before
    fun init() {
        stockPriceRepository = StockPriceAdvancedRepositoryImpl(mongoOperations)
    }

    @Test
    fun `findByNameAndByDateBetween should send the correct query parameters to the database`() {

        // When
        stockPriceRepository.findByNameIgnoreCaseAndByDateBetween("Dave", MIN, MAX)

        // Then
        verify(mongoOperations).find(queryCaptor.capture(), any() as Class<*>?)
        assertEquals("Dave", queryCaptor.value.queryObject["name"].toString())

        val document = queryCaptor.value.queryObject["date"] as Document
        assertEquals(MIN, document["${'$'}gte"])
        assertEquals(MAX, document["${'$'}lte"])
    }
}
