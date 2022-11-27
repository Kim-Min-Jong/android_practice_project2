Shopping
===

## Clean Architecture [공식문서](https://developer.android.com/topic/architecture)

> [복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#architecture)

## Firebase Authentication [공식문서](https://firebase.google.com/docs/auth?hl=ko)

> [복습](https://github.com/Kim-Min-Jong/android_practice_project2/tree/Advanced/advanced/ToDoList#architecture)  
> 기본 세팅 필수

### Google Login

> Firebase의 google login을 통해 인증을 하는 방법이다.

```kotlin
    // 로그인 시 필요한 옵션들
private val gso: GoogleSignInOptions by lazy {
    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
}

// google sign in
private val gsc by lazy {
    GoogleSignIn.getClient(requireActivity(), gso)
}

// firebase auth 객체
private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
```

> 먼저 로그인 시 필요한 각종 옵션을 가져오고 옵션들로 클라이언트를 생성한다.

```kotlin
    // intent 전환 launcher
private val loginLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    Log.e(TAG, result.resultCode.toString())
    Log.e(TAG, Activity.RESULT_OK.toString())
    if (result.resultCode == Activity.RESULT_OK) {
        // 인텐트에서 로그인 정보를 가져옴
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // 가져온 정보에서 세부정보 가져옴
            task.getResult(ApiException::class.java)?.let {
                Log.d(TAG, "firebaseAuthWithGoogle: ${it.id}")
                // 추후 토큰을 저장해야함
                viewModel.saveToken(it.idToken ?: throw Exception())
            } ?: throw Exception()
        } catch (e: Exception) {
            e.printStackTrace()
            handleErrorState()
        }
    }
}
//firebase 로그인
private fun signInGoogle() {
    val signInIntent = gsc.signInIntent
    loginLauncher.launch(signInIntent)
}
```
> 위에서 생성한 gsc객체로 Intent를 만들어 구글 로그인 Intent로 전환한다.
> ```GoogleSignIn.getSignedInAccountFromIntent(result.data)```를 통해 정보(크리덴셜 로그인을 위한 토큰)를 가져온다.

```kotlin
private fun handleLoginState(state: ProfileState.Login) {
    // 유저 정보 가져오기
    val credential = GoogleAuthProvider.getCredential(state.idToken, null)
    // 정보를 가지고 로그인 시도
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(requireActivity()) { task ->
            // 성공하면
            if (task.isSuccessful) {
                // 유저 정보 생성
                val user = firebaseAuth.currentUser
                Log.e(TAG, user.toString())
                viewModel.setUserInfo(user)
            } else {
                viewModel.setUserInfo(null)
                requireContext().toast("로그아웃이 되어 재로그인 필요합니다.")
                Log.w(TAG, "signInWithCredential:failure", task.exception)
            }
        }
}
```
> 가져온 토큰을 통해 유저 auth 정보를 가져오고, 그것을 통해 구글 로그인을 시도한다.



#### recycler view, collapse app bar layout 등은 다음 참고 (이전에 했던 것들)
> [recycle view](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#recyclerview-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)  
> [Custom appbar](https://github.com/Kim-Min-Jong/android_practice_project2/tree/intermediate/intermediate/BookReview#recyclerview-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)  
> [collapseToolbar Layout](https://developer.android.com/reference/com/google/android/material/appbar/CollapsingToolbarLayout)  
