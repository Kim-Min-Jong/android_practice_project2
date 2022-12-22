package com.fc.subwayarrivalinfo.presentation.stationarrivals

import com.fc.subwayarrivalinfo.data.repository.StationRepository
import com.fc.subwayarrivalinfo.domain.Station
import com.fc.subwayarrivalinfo.presentation.stationarrivals.StationArrivalsContract
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class StationArrivalsPresenter(
    private val view: StationArrivalsContract.View,
    private val station: Station,
    private val stationRepository: StationRepository
) : StationArrivalsContract.Presenter {

    override val scope = MainScope()

    override fun onViewCreated() {
        fetchStationArrivals()
    }

    override fun onDestroyView() {}

    override fun fetchStationArrivals() {
        scope.launch {
            try {
                view.showLoadingIndicator()
                view.showStationArrivals(stationRepository.getStationArrivals(station.name))
            } catch (exception: Exception) {
                exception.printStackTrace()
                view.showErrorDescription(exception.message ?: "알 수 없는 문제가 발생했어요 😢")
            } finally {
                view.hideLoadingIndicator()
            }
        }
    }
    override fun toggleStationFavorite() {
        scope.launch {
            // 원본데이터 손상 방지를 위해 copyrighted
            stationRepository.updateStation(station.copy(isFavorited = !station.isFavorited))
        }
    }
}