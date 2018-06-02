package com.brocoding.stocks.api.batch

import com.brocoding.stocks.api.stockdata.domain.DataType.ADJ_CLOSE
import com.brocoding.stocks.api.stockdata.domain.DataType.CLOSE
import com.brocoding.stocks.api.stockdata.domain.DataType.HIGH
import com.brocoding.stocks.api.stockdata.domain.DataType.LOW
import com.brocoding.stocks.api.stockdata.domain.DataType.OPEN
import com.brocoding.stocks.api.stockdata.domain.DataType.VOLUME
import com.brocoding.stocks.api.stockdata.domain.StockData
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParameters
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.batch.item.data.MongoItemWriter
import org.springframework.batch.item.file.FlatFileItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.MongoTemplate
import java.time.LocalDate.parse
import java.time.Year
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField


/**
 * Created by Dave van Hooijdonk on 29-5-2018.
 */
@EnableBatchProcessing
@Configuration
class BatchConfiguration(@Value("stocks/*.csv") val resources: Array<Resource>,
                         val jobBuilderFactory: JobBuilderFactory,
                         val stepBuilderFactory: StepBuilderFactory,
                         val jobLauncher: JobLauncher,
                         val jobRegistry: JobRegistry,
                         val mongoTemplate: MongoTemplate) {

    companion object {
        private val formatter = DateTimeFormatterBuilder()
                .appendPattern("M/d/")
                .appendValueReduced(ChronoField.YEAR, 2, 2, Year.now().value - 80)
                .toFormatter()
    }

    @EventListener(ApplicationReadyEvent::class)
    fun onStartUp() {
        jobRegistry.jobNames.forEach {
            jobRegistry.getJob(it).also {
                jobLauncher.run(it, JobParameters())
            }
        }
    }

    @Bean
    fun jobRegistryBeanPostProcessor(): JobRegistryBeanPostProcessor = JobRegistryBeanPostProcessor()
            .apply { setJobRegistry(jobRegistry) }

    @Bean
    fun readCSVFileJob(step: Step): Job {
        return jobBuilderFactory.get("readCSVFileJob")
                .start(step)
                .build()
    }

    @Bean
    fun step(multiFileReader: ItemReader<StockDataCSV>,
             processor: ItemProcessor<StockDataCSV, StockData>,
             writer: ItemWriter<StockData>): Step {

        return stepBuilderFactory
                .get("step")
                .chunk<StockDataCSV, StockData>(10)
                .reader(multiFileReader)
                .processor(processor)
                .writer(writer)
                .build()
    }

    @Bean
    fun multiFileReader(): ItemReader<StockDataCSV> {
        return MultiResourceItemReaderBuilder<StockDataCSV>()
                .name("multiCsvReader")
                .resources(resources)
                .delegate(reader())
                .build()
    }

    @Bean
    fun reader(): FlatFileItemReader<StockDataCSV> {
        return FlatFileItemReaderBuilder<StockDataCSV>()
                .name("singleCsvReader")
                .delimited()
                .names(arrayOf("date", "open", "high", "low", "close", "adjClose", "volume"))
                .fieldSetMapper(BeanWrapperFieldSetMapper<StockDataCSV>().apply {
                    setTargetType(StockDataCSV::class.java)
                })
                .linesToSkip(1)
                .build()
    }

    @Bean
    fun processor(): ItemProcessor<StockDataCSV, StockData> = ItemProcessor {
        val (name, date, open, high, low, close, closeAdj, volume) = it
        val properDate = parse(date!!, formatter)
        val dailyPrices = mapOf(
                OPEN to open!!,
                HIGH to high!!,
                LOW to low!!,
                CLOSE to close!!,
                ADJ_CLOSE to closeAdj!!,
                VOLUME to volume!!
        )
        StockData(name?.toLowerCase()!!, properDate, dailyPrices)
    }

    @Bean
    fun writer(): MongoItemWriter<StockData> {
        return MongoItemWriter<StockData>().apply {
            setTemplate(mongoTemplate)
            setCollection("StockData")
        }
    }
}