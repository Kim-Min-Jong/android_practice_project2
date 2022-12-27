package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.entity.ShippingCompany

interface ShippingCompanyRepository {
    // 목록 가져오기 (리스트로)
    suspend fun getShippingCompanies(): List<ShippingCompany>

    // 추천 택배사 가져오기
    suspend fun getRecommendShippingCompany(invoice: String): ShippingCompany?
}