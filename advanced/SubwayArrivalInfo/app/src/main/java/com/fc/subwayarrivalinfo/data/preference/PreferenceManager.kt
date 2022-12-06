package com.fc.subwayarrivalinfo.data.preference

// Preference 기능에 쓸 것을 미리 선언 - 추후 재정의
interface PreferenceManager {

    fun getLong(key: String): Long?

    fun putLong(key: String, value: Long)
}