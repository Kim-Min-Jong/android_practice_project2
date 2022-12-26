package com.fc.trackingdelivery.data.repository

import com.fc.trackingdelivery.data.api.SweetTrackerApi
import com.fc.trackingdelivery.data.db.ShippingCompanyDao
import com.fc.trackingdelivery.data.entity.ShippingCompany
import com.fc.trackingdelivery.data.preference.PreferenceManager

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ShippingCompanyRepositoryImpl(
    private val trackerApi: SweetTrackerApi,
    private val shippingCompanyDao: ShippingCompanyDao,
    private val preferenceManager: PreferenceManager,
    private val dispatcher: CoroutineDispatcher
) : ShippingCompanyRepository {

    override suspend fun getShippingCompanies(): List<ShippingCompany> = withContext(dispatcher) {
        // 현재 시간
        val currentTimeMillis = System.currentTimeMillis()
        // 디비에 저장된 시간
        val lastDatabaseUpdatedTimeMillis = preferenceManager.getLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS)

        // 디비에 저장된 시간이 없거나 차이가 일주일 미만 이면 (캐시 데이터 새로고침 요망)
        if (lastDatabaseUpdatedTimeMillis == null ||
            CACHE_MAX_AGE_MILLIS < (currentTimeMillis - lastDatabaseUpdatedTimeMillis)
        ) {
            // 다시 api를 통해 회사 정보를 가져와 db에 저장하고 디비 저장 시간을 업데이트
            val shippingCompanies = trackerApi.getShippingCompanies()
                .body()
                ?.shippingCompanies
                ?: emptyList()
            shippingCompanyDao.insert(shippingCompanies)
            preferenceManager.putLong(KEY_LAST_DATABASE_UPDATED_TIME_MILLIS, currentTimeMillis)
        }

        // 회사 전체 목록 가져옴
        shippingCompanyDao.getAll()
    }

    companion object {
        private const val KEY_LAST_DATABASE_UPDATED_TIME_MILLIS = "KEY_LAST_DATABASE_UPDATED_TIME_MILLIS"
        private const val CACHE_MAX_AGE_MILLIS = 1000L * 60 * 60 * 24 * 7
    }
}