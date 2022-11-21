package com.fc.shopping.di

import com.fc.shopping.data.network.buildOkHttpClient
import com.fc.shopping.data.network.provideGsonConverterFactory
import com.fc.shopping.data.network.provideProductApiService
import com.fc.shopping.data.network.provideProductRetrofit
import com.fc.shopping.data.repository.DefaultProductRepository
import com.fc.shopping.data.repository.ProductRepository
import com.fc.shopping.domain.GetProductItemUseCase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val appModule = module {
    single{ provideGsonConverterFactory() }
    single{ buildOkHttpClient() }
    single{ provideProductRetrofit(get(), get()) }
    single{ provideProductApiService(get()) }

    // CoroutineDispatcher
    single{ Dispatchers.Main }
    single{ Dispatchers.IO }

    //repositories
    single<ProductRepository> { DefaultProductRepository(get(), get())}

    //UseCases
    factory { GetProductItemUseCase(get()) }
}