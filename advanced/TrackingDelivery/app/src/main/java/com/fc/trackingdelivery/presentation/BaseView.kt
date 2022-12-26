package com.fc.trackingdelivery.presentation

interface  BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}