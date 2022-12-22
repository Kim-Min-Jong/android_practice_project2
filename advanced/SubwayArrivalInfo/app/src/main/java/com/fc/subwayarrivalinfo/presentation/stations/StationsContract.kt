package com.fc.subwayarrivalinfo.presentation.stations

import com.fc.subwayarrivalinfo.domain.Station
import com.fc.subwayarrivalinfo.presentation.BasePresenter
import com.fc.subwayarrivalinfo.presentation.BaseView

interface StationsContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showStations(stations: List<Station>)
    }

    interface Presenter : BasePresenter {
        fun filterStations(query: String)
    }
}