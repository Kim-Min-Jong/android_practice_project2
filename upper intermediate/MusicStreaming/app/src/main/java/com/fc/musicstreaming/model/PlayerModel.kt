package com.fc.musicstreaming.model

// MusicModel의 기능을 분산하기위해 만듦
data class PlayerModel(
    private val playMusicList: List<MusicModel> = emptyList(),
    var currentPosition: Int = -1,
    var isWatchedPlayListView: Boolean = true
) {
    // 인덱스와 모델이 맞으면 그때 음악실행하도록
    // copy를 통해 같은 모델을 만들고 isPlaying 상태만 바꿔주어 UI변화를 줄 수 있게함
    fun getAdapterModels(): List<MusicModel> {
        return playMusicList.mapIndexed { index, musicModel ->
            val newItem = musicModel.copy(
                isPlaying = index == currentPosition
            )
            newItem
        }
    }

    fun updateCurrentPosition(musicModel: MusicModel) {
        currentPosition = playMusicList.indexOf(musicModel)
    }

    // 다음 음악
    fun nextMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null

        currentPosition = if((currentPosition + 1) == playMusicList.size) 0 else currentPosition + 1

        return playMusicList[currentPosition]
    }

    fun prevMusic(): MusicModel? {
        if(playMusicList.isEmpty()) return null
        currentPosition = if((currentPosition - 1) < 0)  playMusicList.lastIndex  else currentPosition - 1
        return playMusicList[currentPosition]
    }
}
