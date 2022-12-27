package com.fc.trackingdelivery.presentation.addtrackingitem

import com.fc.trackingdelivery.data.entity.ShippingCompany
import com.fc.trackingdelivery.presentation.BasePresenter
import com.fc.trackingdelivery.presentation.BaseView

class AddTrackingItemsContract {
    interface View : BaseView<Presenter> {

        // 택배사 정보를 불러올 때 로딩 바 보여주기
        fun showShippingCompaniesLoadingIndicator()

        // 택배사 정보를 다 불러온 후 로딩 바 숨기기
        fun hideShippingCompaniesLoadingIndicator()

        // 추적 정보를 저장할 때 로딩바 보여주기
        fun showSaveTrackingItemIndicator()

        // 추적 정보 저장이 끝날 때 로딩바 숨기기
        fun hideSaveTrackingItemIndicator()

        // 택배사 추천 api를 불러올때 로딩바 보여주기
        fun showRecommendCompanyLoadingIndicator()

        // 택배사 추천 api를 불러오면 로딩바 숨기기
        fun hideRecommendCompanyLoadingIndicator()

        // 불러온 택배사 보여주기
        fun showCompanies(companies: List<ShippingCompany>)

        // 불러온 추천 택배사 보여주기기
       fun showRecommendCompany(company: ShippingCompany)

        // 저장버튼 enable (운송장 및 택배사 선택 유)
        fun enableSaveButton()
        // 저장버튼 disable (운송장 및 택배사 선택 무)
        fun disableSaveButton()

        fun showErrorToast(message: String)

        fun finish()
    }

    interface Presenter : BasePresenter {

        var invoice: String?
        var shippingCompanies: List<ShippingCompany>?
        var selectedShippingCompany: ShippingCompany?

        // 택배사 가져옴
        fun fetchShippingCompanies()
        // 클립보드의 운송장을 기반으로 택배사를 추천
        fun fetchRecommendShippingCompany()
        // 선택한 택배사로 변경
        fun changeSelectedShippingCompany(companyName: String)
        // 운송장 선택
        fun changeShippingInvoice(invoice: String)
        // 정보 저장
        fun saveTrackingItem()
    }
}