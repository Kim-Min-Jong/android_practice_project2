package com.fc.subwayarrivalinfo.presentation.stations

import com.fc.subwayarrivalinfo.data.repository.StationRepository
import com.fc.subwayarrivalinfo.domain.Station
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StationsPresenter(
    private val view: StationsContract.View,
    private val stationRepository: StationRepository
) : StationsContract.Presenter {

    override val scope: CoroutineScope = MainScope()

    // 옵저빙을 위해 사용

    // 검색정보
    private val queryString: MutableStateFlow<String> = MutableStateFlow("")
    // 역정보보
   private val stations: MutableStateFlow<List<Station>> = MutableStateFlow(emptyList())

    init {
        observeStations()
    }

    override fun onViewCreated() {
        scope.launch {
            view.showStations(stations.value)
            stationRepository.refreshStations()
        }
    }

    override fun onDestroyView() {}

    override fun filterStations(query: String) {
        scope.launch {
            queryString.emit(query)
        }
    }

    private fun observeStations() {
        stationRepository
            .stations
             // station 정보들 중 검색어가 포함된 최근 값을 찾아 가져옴
            .combine(queryString) { stations, query ->
                // 쿼리가 없다면 station 전체목록
                if (query.isBlank()) {
                    stations
                }
                // 쿼리(검색어)가 있다면 포함된 값 필터링해서 반환
                else {
                    stations.filter { it.name.contains(query) }
                }
            }
            // 값이 처음 바뀔때
            .onStart { view.showLoadingIndicator() }
            // 바뀔 때 마다 실행행
           .onEach {
               if (it.isNotEmpty()) {
                   view.hideLoadingIndicator()
               }
               stations.value = it
               view.showStations(it)
           }
            // 에러 시
            .catch {
                it.printStackTrace()
                view.hideLoadingIndicator()
            }
            .launchIn(scope)
    }
}