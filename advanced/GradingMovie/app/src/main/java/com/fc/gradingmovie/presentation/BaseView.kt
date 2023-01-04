package com.fc.gradingmovie.presentation

interface BaseView<PresenterT : BasePresenter> {

    val presenter: PresenterT
}