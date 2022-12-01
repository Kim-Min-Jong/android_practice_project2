package com.fc.usedtrade.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fc.usedtrade.Application.Companion.appContext
import kotlinx.coroutines.launch

class GalleryViewModel: ViewModel() {
    private val galleryPhotoRepository by lazy{ GalleryPhotoRepository(appContext!!) }
    val galleryStateLiveData = MutableLiveData<GalleryState>(GalleryState.Uninitialized)
     // 불러온 사진들을 담을 리스트
     private lateinit var photoList: MutableList<GalleryPhoto>

     // 리스트 불러오기
     fun fetchData() = viewModelScope.launch {
         setState(
             GalleryState.Loading
         )
         photoList = galleryPhotoRepository.getAllPhotos()
         setState(
             GalleryState.Success(
                 photoList = photoList
             )
         )
     }

    private fun setState(state: GalleryState) {
        galleryStateLiveData.postValue(state)
    }

    // 사진을 선택할 떄 선택된 상태로 만들기 위한 함수
    fun selectPhoto(galleryPhoto: GalleryPhoto) {
        val findGalleryPhoto = photoList.find { it.id == galleryPhoto.id }
        findGalleryPhoto?.let { photo ->
            photoList[photoList.indexOf(photo)] =
                photo.copy(
                    isSelected = photo.isSelected.not()
                )
            setState(
                GalleryState.Success(
                    photoList = photoList
                )
            )
        }
    }

    // 선택된 사진들을 확정지어 글작성으로 넘겨주기 위한 상태로 만들어주는 함수
    fun confirmCheckedPhotos() {
        setState(
            GalleryState.Loading
        )
        setState(
            GalleryState.Confirm(
                photoList = photoList.filter { it.isSelected }
            )
        )
    }
}