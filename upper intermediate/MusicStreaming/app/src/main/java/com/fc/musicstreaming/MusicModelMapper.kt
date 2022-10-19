package com.fc.musicstreaming

import com.fc.musicstreaming.model.MusicModel
import com.fc.musicstreaming.model.PlayerModel
import com.fc.musicstreaming.service.MusicDto
import com.fc.musicstreaming.service.MusicEntity

fun MusicEntity.mapper(id: Long) =
    MusicModel(
        id, track, streamUrl, artist, coverUrl
    )

fun MusicDto.mapper(): PlayerModel =
    PlayerModel(
        playMusicList = musics.mapIndexed { idx, entity ->
                entity.mapper(idx.toLong())
        }
    )