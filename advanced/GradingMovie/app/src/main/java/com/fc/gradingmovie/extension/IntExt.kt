package com.fc.gradingmovie.extension

// 숫자가 커질 때 K, M을 통해 천, 백만 자리 변환
fun Int.toAbbreviatedString(): String = when (this) {
    in 0..1_000 -> {
        this.toString()
    }
    in 1_000..1_000_000 -> {
        "${(this / 1_000f).toDecimalFormatString("#.#")}K"
    }
    else -> {
        "${(this / 1_000_000f).toDecimalFormatString("#.#")}M"
    }
}