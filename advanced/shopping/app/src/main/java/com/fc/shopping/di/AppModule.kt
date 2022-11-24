package com.fc.shopping.di

import com.fc.shopping.data.db.dao.provideDB
import com.fc.shopping.data.db.dao.provideToDoDao
import com.fc.shopping.data.network.buildOkHttpClient
import com.fc.shopping.data.network.provideGsonConverterFactory
import com.fc.shopping.data.network.provideProductApiService
import com.fc.shopping.data.network.provideProductRetrofit
import com.fc.shopping.data.preference.PreferenceManager
import com.fc.shopping.data.repository.DefaultProductRepository
import com.fc.shopping.data.repository.ProductRepository
import com.fc.shopping.domain.*
import com.fc.shopping.domain.DeleteOrderedProductListUseCase
import com.fc.shopping.domain.GetOrderedProductListUseCase
import com.fc.shopping.domain.GetProductItemUseCase
import com.fc.shopping.domain.GetProductListUseCase
import com.fc.shopping.domain.OrderProductItemUseCase
import com.fc.shopping.presentation.detail.ProductDetailViewModel
import com.fc.shopping.presentation.list.ProductListViewModel
import com.fc.shopping.presentation.main.MainViewModel
import com.fc.shopping.presentation.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single{ provideGsonConverterFactory() }
    single{ buildOkHttpClient() }
    single{ provideProductRetrofit(get(), get()) }
    single{ provideProductApiService(get()) }

    //database 주입
    single{ provideDB(androidApplication()) }
    single{ provideToDoDao(get()) }

    // CoroutineDispatcher
    single{ Dispatchers.Main }
    single{ Dispatchers.IO }

    //repositories
    single<ProductRepository> { DefaultProductRepository(get(), get(), get())}

    // sharedPreference
    single{ PreferenceManager(androidApplication()) }

    //UseCases
    factory { GetProductItemUseCase(get()) }
    factory { GetProductListUseCase(get()) }
    factory { OrderProductItemUseCase(get()) }
    factory { GetOrderedProductListUseCase(get()) }
    factory { DeleteOrderedProductListUseCase(get()) }

    //ViewModels
    viewModel { MainViewModel() }
    viewModel { ProfileViewModel(get(), get(), get()) }
    viewModel { ProductListViewModel(get()) }
    viewModel { (productId: Long) -> ProductDetailViewModel(productId, get(), get()) }
}