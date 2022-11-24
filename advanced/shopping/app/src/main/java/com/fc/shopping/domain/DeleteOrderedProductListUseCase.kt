package com.fc.shopping.domain

import com.fc.shopping.data.repository.ProductRepository

internal class DeleteOrderedProductListUseCase(
    private val productRepository: ProductRepository
): UseCase{
    suspend operator fun invoke() {
        return productRepository.deleteAll()
    }
}