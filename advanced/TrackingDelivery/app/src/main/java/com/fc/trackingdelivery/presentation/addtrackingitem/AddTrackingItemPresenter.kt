package com.fc.trackingdelivery.presentation.addtrackingitem

import com.fc.trackingdelivery.data.entity.ShippingCompany
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.data.repository.ShippingCompanyRepository
import com.fc.trackingdelivery.data.repository.TrackingItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddTrackingItemPresenter(
    private val view: AddTrackingItemsContract.View,
    private val shippingCompanyRepository: ShippingCompanyRepository,
    private val trackerRepository: TrackingItemRepository
) : AddTrackingItemsContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    override var invoice: String? = null
    override var shippingCompanies: List<ShippingCompany>? = null
    override var selectedShippingCompany: ShippingCompany? = null

    // fragment lifecycle - 화면 생성될때
    override fun onViewCreated() {
        // 택배사 목록 불러오기
        fetchShippingCompanies()
    }

    override fun onDestroyView() {}

    override fun fetchShippingCompanies() {
        scope.launch {
            // 로딩
            view.showShippingCompaniesLoadingIndicator()
            // 택배사 없으면 불러오기
            if (shippingCompanies.isNullOrEmpty()) {
                shippingCompanies = shippingCompanyRepository.getShippingCompanies()
            }

            // 불러온 택배사를 뷰에 보여주기
            shippingCompanies?.let { view.showCompanies(it) }

            // 로딩 숨기기기
           view.hideShippingCompaniesLoadingIndicator()
        }
    }

    override fun changeSelectedShippingCompany(companyName: String) {
        // 선택된 회사는 회사들 중에서 선택회사 이름을 찾아 변수어 넣어줌
        selectedShippingCompany = shippingCompanies?.find { it.name == companyName }
        // 택배사 운송장 번호가 있는 지 확인하고 버튼 활성화 여부 결정
        enableSaveButtonIfAvailable()
    }

    override fun changeShippingInvoice(invoice: String) {
        // 입력된 운송장을 변수에 저장
        this.invoice = invoice
        // 택배사 운송장 번호가 있는 지 확인하고 버튼 활성화 여부 결정
        enableSaveButtonIfAvailable()
    }

    override fun saveTrackingItem() {
        scope.launch {
            try {
                view.showSaveTrackingItemIndicator()
                // 운송장과 선택된 회사로 추적정보 저장
                trackerRepository.saveTrackingItem(
                    TrackingItem(
                        invoice!!,
                        selectedShippingCompany!!
                    )
                )
                view.finish()
            } catch (exception: Exception) {
                view.showErrorToast(exception.message ?: "서비스에 문제가 생겨서 운송장을 추가하지 못했어요 😢")
            } finally {
                view.hideSaveTrackingItemIndicator()
            }
        }
    }

    private fun enableSaveButtonIfAvailable() {
        if (!invoice.isNullOrBlank() && selectedShippingCompany != null) {
            view.enableSaveButton()
        } else {
            view.disableSaveButton()
        }
    }
}