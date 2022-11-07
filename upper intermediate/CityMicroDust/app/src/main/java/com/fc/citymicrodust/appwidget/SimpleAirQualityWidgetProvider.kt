package com.fc.citymicrodust.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

// 이 클래스는 브로드 캐스트 리시버이기 떄문에 시스템에의해 언제든지 취소 당할 수 있다.
class SimpleAirQualityWidgetProvider: AppWidgetProvider() {
    // 그래서 업데이트 될 때, 서비스를 시작하여 강제 취소를 막아야한다.
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}