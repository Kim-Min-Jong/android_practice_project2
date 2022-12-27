package com.fc.trackingdelivery.presentation.trackinghistory

import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.presentation.BasePresenter
import com.fc.trackingdelivery.presentation.BaseView

class TrackingHistoryContract {
    interface View : BaseView<Presenter> {

        fun hideLoadingIndicator()

        // 택배정보를 통해 추적 상세정보 보여주기
        fun showTrackingItemInformation(trackingItem: TrackingItem, trackingInformation: TrackingInformation)

        fun finish()
    }

    interface Presenter : BasePresenter {

        fun refresh()

        // 추적 정보 삭제
        fun deleteTrackingItem()
    }
}