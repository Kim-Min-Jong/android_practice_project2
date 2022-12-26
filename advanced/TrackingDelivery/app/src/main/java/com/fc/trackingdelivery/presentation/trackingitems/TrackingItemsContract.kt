package com.fc.trackingdelivery.presentation.trackingitems

import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.presentation.BasePresenter
import com.fc.trackingdelivery.presentation.BaseView

class TrackingItemsContract {

    interface View : BaseView<Presenter> {

        // 로딩 때 보여줄 것
        fun showLoadingIndicator()

        // 로딩 숨기기
        fun hideLoadingIndicator()

        // 데이터가 없을 때 보여줄 거
        fun showNoDataDescription()

        // 아이템 정보 보여줄 거
        fun showTrackingItemInformation(trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>>)
    }

    interface Presenter : BasePresenter {
        // 데이터
        var trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>>

        // 새로고침
        fun refresh()
    }
}