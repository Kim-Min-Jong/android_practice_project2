package com.fc.subwayarrivalinfo.presentation

interface BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}