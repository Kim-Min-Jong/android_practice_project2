package com.fc.musicstreaming.service

import android.util.Log
import org.jsoup.Jsoup
import java.lang.StringBuilder


fun webCrawling(): MusicDto {
    val randomPage = (1..61).random()
    Log.e("page", randomPage.toString())
    val url = "https://ncs.io/music?page=${randomPage}"
    val doc = Jsoup.connect(url).get()

//    val elementArtist = doc.select("div.container-fluid div.row div[class=\"col-lg-2 item\"] a div.bottom span")
    val element = doc.select("div.container-fluid div.row div[class=\"col-lg-2 item\"] div.options div[class=\"row align-items-center\"] div[class=\"col-6 col-lg-6\"] a")

    val musicList = mutableListOf<MusicEntity>()

    for(elem in element) {
//        Log.e("MainActivity", elem.attr("data-url"))
//        Log.e("MainActivity", elem.attr("data-cover"))
//        Log.e("MainActivity", elem.attr("data-track"))
        val artist = elem.attr("data-artist").split(", ")
        val fullArtist = StringBuilder()
        artist.forEach {
            fullArtist.append(Jsoup.parse(it).text() + " & ")
        }
//        Log.e("MainActivity", fullArtist.removeSuffix(" & ").toString())

        val entity = MusicEntity(elem.attr("data-track"), elem.attr("data-url"), fullArtist.removeSuffix(" & ").toString(), elem.attr("data-cover"))
//        Log.e("MainActivity", entity.toString())
        musicList.add(entity)
    }

    return MusicDto(musicList)
}