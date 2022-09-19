package com.fc.pushalarmreciever

enum class NotificationType(val title:String, val id:Int) {
    NORMAL("일반알림", 0),
    EXPANDABLE("확장알림", 1),
    CUSTOM("커스텀알림", 3),
}