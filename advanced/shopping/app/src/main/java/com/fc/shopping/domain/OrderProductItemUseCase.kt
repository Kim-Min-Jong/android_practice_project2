package com.fc.shopping.domain

import com.fc.shopping.data.entity.product.ProductEntity
import com.fc.shopping.data.repository.ProductRepository

internal class OrderProductItemUseCase(
    private val productRepository: ProductRepository
) : UseCase {
    suspend operator fun invoke(productEntity: ProductEntity): Long =
        productRepository.insertProductItem(productEntity)

}