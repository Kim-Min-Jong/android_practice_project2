package com.fc.musicstreaming

import com.fc.musicstreaming.model.MusicModel
import com.fc.musicstreaming.service.MusicEntity

fun MusicEntity.mapper(id: Long) =
    MusicModel(
        id, track, streamUrl, artist, coverUrl
    )

