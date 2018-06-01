package com.brocoding.stocks.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Created by Dave van Hooijdonk on 28-5-2018.
 */
@SpringBootApplication
class FordStocksApi

fun main(args: Array<String>) {
    SpringApplication.run(FordStocksApi::class.java, *args)
}