package com.fc.subwayarrivalinfo.presentation.stationarrivals

import com.fc.subwayarrivalinfo.domain.ArrivalInformation
import com.fc.subwayarrivalinfo.presentation.BasePresenter
import com.fc.subwayarrivalinfo.presentation.BaseView

interface StationArrivalsContract {

    interface View : BaseView<Presenter> {

        fun showLoadingIndicator()

        fun hideLoadingIndicator()

        fun showErrorDescription(message: String)

        fun showStationArrivals(arrivalInformation: List<ArrivalInformation>)
    }

    interface Presenter : BasePresenter {

        fun fetchStationArrivals()
    }
}