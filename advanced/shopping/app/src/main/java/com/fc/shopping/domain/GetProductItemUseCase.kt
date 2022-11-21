package com.fc.shopping.domain

import com.fc.shopping.data.entity.product.ProductEntity
import com.fc.shopping.data.repository.ProductRepository

internal class GetProductItemUseCase(
    private val productRepository: ProductRepository
) : UseCase {
    suspend operator fun invoke(productId: Long): ProductEntity? =
        productRepository.getProductItem(productId)

}