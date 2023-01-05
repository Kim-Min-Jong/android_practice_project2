package com.fc.gradingmovie.extension

import java.text.DecimalFormat

// 숫자를 특정 format에 대해서 변환함
fun Float.toDecimalFormatString(format: String): String = DecimalFormat(format).format(this)