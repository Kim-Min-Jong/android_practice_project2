package com.fc.trackingdelivery.presentation.trackingitems

import com.fc.trackingdelivery.data.entity.TrackingInformation
import com.fc.trackingdelivery.data.entity.TrackingItem
import com.fc.trackingdelivery.data.repository.TrackingItemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

// repo를 받아 Contract를 구체화
class TrackingItemsPresenter(
    private val view: TrackingItemsContract.View,
    private val trackingItemRepository: TrackingItemRepository
) : TrackingItemsContract.Presenter {

    override var trackingItemInformation: List<Pair<TrackingItem, TrackingInformation>> = emptyList()

    override val scope: CoroutineScope = MainScope()

    override fun onViewCreated() {
        fetchTrackingInformation()
    }

    override fun onDestroyView() {}

    override fun refresh() {
        fetchTrackingInformation(true)
    }

    // 정보 가져오기
    private fun fetchTrackingInformation(forceFetch: Boolean = false) = scope.launch {
        try {
            // 로딩 바 보여주고
            view.showLoadingIndicator()

            // 아이템이 없고 강제fetch가 활성화 되어있을 땐 api를 통해 다시 가져옴
            if (trackingItemInformation.isEmpty() || forceFetch) {
                trackingItemInformation = trackingItemRepository.getTrackingItemInformation()
            }

            // 가져온 정보가 없으면 비었을 때의 뷰를 보여줌
            if (trackingItemInformation.isEmpty()) {
                view.showNoDataDescription()
            } else {  // 아니면 정보를 보여줌 (리사이클러뷰)
                view.showTrackingItemInformation(trackingItemInformation)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            // 로딩이 끝나면 바 지우기
            view.hideLoadingIndicator()
        }
    }
}