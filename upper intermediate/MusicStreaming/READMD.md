Music  Streaming
===

## androidx.constraintlayout.widget.Group

> ConstraintLayout을 사용할 때, 내부의 다양한 위젯들의 동일한 속성을 한 번에 제어해고
> 싶은 경우가 있다. 보통, ```binding.xxx``` 를 통해 위젯에 접근하고 속성, 리스너등 제어를 하게 되지만,
> 코드가 보기 싫고, 길어질 것이다. 이때, 위젯들을 새로운 레이아웃으로 감싸 제어를 할 수 있겠지만, Constraint
> layout을 사용하는 의미가 퇴색 될 것이다.
> 이때, Group을 사용하면 된다.
> Group에 한 번에 제어할 속성을 모두 선언한 뒤, 코드상에서 제어할 수 있다.

```xml

<androidx.constraintlayout.widget.Group
        android:id="@+id/playerViewGroup"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:visibility="gone"
        tools:visibility="visible"
        app:constraint_referenced_ids="trackTextView, artistTextView, coverImageCardView, bottomBackgroundView, playerSeekBar, playTimeTextView, totalTimeTextView"/>
```

> 묶을 위젯들을 ```app:constraint_referenced_ids```속성에 선언하고

```kotlin
binding?.playerViewGroup?.isVisible = true
```

> 코드에서 Group에 접근하여 속성 등을 제어 할 수 있다.

## **Seekbar** Custom 하기

```xml

<SeekBar
        android:id="@+id/playerSeekBar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:maxHeight="4dp"
        android:minHeight="4dp"
        android:layout_marginBottom="30dp"
        android:layout_marginStart="50dp"
        android:layout_marginEnd="50dp"
        android:paddingStart="0dp"
        android:paddingEnd="0dp"
        android:progressDrawable="@drawable/player_seek_background"
        android:thumb="@drawable/player_seek_thumb"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintBottom_toTopOf="@id/playerView"
        tools:progress="40"
/>
```

> ```android:maxHeight```, ```android:minHeight```: seekBar의 최대, 최소 높이를 지정한다.  
> 강제로 얇게 지정하여 얇게 진행되는 seekBar를 만들 수 있다.
>
> ```android:progressDrawable```: 진행 상황바를 커스텀 xml로 제작하여 적용할 수 있다.
>
> ```android:thumb```: seekBar의 현재 위치를 나타내는 아이콘을 커스텀xml로 제작하여 적용할 수 있다.

### seekBar 제어

```kotlin
private fun updateSeekUi(duration: Long, position: Long) {
    binding?.let {
        it.playListSeekBar.max = (duration / 1000).toInt()
        it.playListSeekBar.progress = (position / 1000).toInt()

        it.playerSeekBar.max = (duration / 1000).toInt()
        it.playerSeekBar.progress = (position / 1000).toInt()
    }
}
```
> seekBar의 max 속성을 지정하고, 현재 상태인 progress를 지정하여 seekBar를 제어한다.
> 단, progress는 한 시점의 값이므로, 쓰레드나 코루틴등 제어를 통해 progress 자주 업데이트해주어야
> 움직이는 seekBar UI를 볼 수 있다.

## ExoPlayer

- Google이 Android SDK 와 별도로 배포되는 오픈소스 프로젝트
- 오디오 및 동영상 재생 가능
- 오디오 및 동영상 재생 관련 강력한 기능들 포함
- 유튜브 앱에서 사용하는 라이브러리
- https://exoplayer.dev/hello-world.html
-

## **Exoplayer** 사용하기 (2)

[복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Upper_intermediate/upper%20intermediate/YouTube#exoplayer-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-%EA%B3%B5%EC%8B%9D%EB%AC%B8%EC%84%9C)

    - custom controller
        - 재생버튼, 이전곡버튼, 다음곡버튼에 exoPlayer를 바인딩시켜 제어할 수 있음

```kotlin
private fun initPlayControlButtons() {
    binding?.playControlImageView?.setOnClickListener {
        val player = this.player ?: return@setOnClickListener

        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }
    binding?.skipNextImageView?.setOnClickListener {
        val nextMusic = model.nextMusic() ?: return@setOnClickListener
        playMusic(nextMusic)
    }
    binding?.skipPrevImageView?.setOnClickListener {
        val prevMusic = model.prevMusic() ?: return@setOnClickListener
        playMusic(prevMusic)
    }
}
```

    - Playlist 
        - 리사이클러 뷰를 통하여 음악 목록을 만들고,클릭시 음악을 실행할 수 있음
        - 리사이클러 뷰의 어탭터에 콜백을 설정해고, 어탭터에서 클릭리스너를 달아 음악 실행

```kotlin
 playListAdapter = PlayListAdapter {
    // 음악 재생
    playMusic(it)
}

inner class ViewHolder(private val binding: ItemMusicBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MusicModel) {
        // ...
        itemView.setOnClickListener {
            callback(item)
        }
    }
}
```

---

### 음악 스트리밍 앱

Retrofit 을 이용하여 재생 목록을 받아와 구성함

재생 목록을 클릭하여 ExoPlayer 를 이용하여 음악을 재생할 수 있음.

이전, 다음 트랙 버튼을 눌러서 이전, 다음 음악으로 재생하고, ui 를 업데이트 할 수 있음.

PlayList 화면과 Player 화면 간의 전환을 할 수 있음.

Seekbar 를 custom 하여 원하는 UI 로 표시할 수 있음.
