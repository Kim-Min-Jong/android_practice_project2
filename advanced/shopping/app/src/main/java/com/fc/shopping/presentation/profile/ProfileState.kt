package com.fc.shopping.presentation.profile

import android.net.Uri
import com.fc.shopping.data.entity.product.ProductEntity

internal sealed class ProfileState {

    object UnInitialized: ProfileState()

    object Loading: ProfileState()

    // 로그인해서 토큰을 받아오기 위해 idToken설정
    data class Login(
        val idToken: String
    ): ProfileState()

    // 해당 계정이 로그인이 되었냐 안되었냐 판단해야하기 때문에 두 가지 세부상태 설정
    sealed class Success: ProfileState() {

        // 된 상태
        data class Registered(
            val userName: String,
            val profileImgUri: Uri?,
            val productList: List<ProductEntity> = listOf()
        ): Success()

        // 안된 상태
        object NotRegistered: Success()

    }

    object Error: ProfileState()

}