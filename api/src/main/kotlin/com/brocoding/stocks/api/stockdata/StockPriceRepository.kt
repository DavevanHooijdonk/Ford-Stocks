package com.brocoding.stocks.api.stockdata

import com.brocoding.stocks.api.stockdata.domain.StockData
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
@Repository
interface StockPriceRepository : MongoRepository<StockData, String>, StockPriceAdvancedRepository


interface StockPriceAdvancedRepository {

    /**
     * @param name of the company
     * @param start starting date of the request time period
     * @param end end date of the requested period
     *
     * @return a List of StockData retrieved from the mongoDB
     */
    fun findByNameIgnoreCaseAndByDateBetween(name: String, start: LocalDate, end: LocalDate): List<StockData>

}


class StockPriceAdvancedRepositoryImpl(private val mongoOperation: MongoOperations) : StockPriceAdvancedRepository {

    override fun findByNameIgnoreCaseAndByDateBetween(name: String, start: LocalDate, end: LocalDate): List<StockData> {

        val query = Query().apply {
            addCriteria(Criteria.where("name").regex(name, "i"))
            addCriteria(Criteria.where("date").gte(start).lte(end))
        }

        return mongoOperation.find(query, StockData::class.java)
    }
}