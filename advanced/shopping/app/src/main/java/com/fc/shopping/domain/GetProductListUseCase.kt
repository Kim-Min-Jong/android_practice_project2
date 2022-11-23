package com.fc.shopping.domain

import com.fc.shopping.data.entity.product.ProductEntity
import com.fc.shopping.data.repository.ProductRepository

internal class GetProductListUseCase(
    private val productRepository: ProductRepository
) : UseCase {
    suspend operator fun invoke(): List<ProductEntity> =
        productRepository.getProductList()

}