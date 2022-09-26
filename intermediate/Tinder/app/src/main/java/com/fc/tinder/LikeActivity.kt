package com.fc.tinder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.fc.tinder.databinding.ActivityLikeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class LikeActivity : AppCompatActivity(), CardStackListener {
    private val auth = FirebaseAuth.getInstance()
    private var binding: ActivityLikeBinding? = null
    // 전체 유저 디비
    private lateinit var userDB: DatabaseReference

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val manager by lazy{
        CardStackLayoutManager(this, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLikeBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        Log.e("AUTH", auth.currentUser?.uid.toString())
        userDB = Firebase.database.reference.child(DBKey.USERS)

        val currentUserDB = userDB.child(getCurrentUserId())
        // 단일 값 이벤트만 실행 (한번만 불러옴) (내 정보만 가져오기 위하여 한번만 실행) -- userId
        currentUserDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // snapshot - 현재 유저 정보
                if(snapshot.child(DBKey.NAME).value == null){
                    showNameInputPopUp()
                    return
                }
                // 유저 정보를 갱신해라
                getUnSelectedUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        initCardStackView()
        initMatchedListBtn()
        initSignOutBtn()

    }

    private fun initSignOutBtn() {
        binding?.signOutButton?.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun initMatchedListBtn() {
        binding?.matchListButton?.setOnClickListener {
            startActivity(Intent(this, MatchedUserActivity::class.java))
        }
    }

    private fun initCardStackView() {
        binding?.cardStackView?.layoutManager = manager
        binding?.cardStackView?.adapter = adapter

    }

    private fun getUnSelectedUsers() {
        userDB.addChildEventListener(object: ChildEventListener {
            // 새로운 상대가 생길 시
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 지금아이디가 내가 아니고, 상대방의 like목록에 내가 없고, 상대방의 dislike목록에 내가 없을때인 것만 보여줌
                // -->한번도 선택 안된 유저
                if(snapshot.child(DBKey.USER_ID).value != getCurrentUserId()
                    && snapshot.child(DBKey.LIKED_BY).child(DBKey.LIKE).hasChild(getCurrentUserId()).not()
                    && snapshot.child(DBKey.LIKED_BY).child(DBKey.DISLIKE).hasChild(getCurrentUserId()).not()){

                    val userId = snapshot.child(DBKey.USER_ID).value.toString()
                    var name = "undecided" // 아이디는 있는데 닉네임 설정을 아직 안했을 때 디폴트값
                    if(snapshot.child(DBKey.NAME).value != null){
                        name = snapshot.child(DBKey.NAME).value.toString() // 닉네임 있으면 재초기화
                    }
                    cardItems.add(CardItem(userId, name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()
                }
            }

            // 상대방의 데이터가 바뀌었을 시
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                cardItems.find{ it.userId == snapshot.key }?.let{
                    it.name = snapshot.child(DBKey.NAME).value.toString()
                }
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun showNameInputPopUp() {
        val editText = EditText(this)

        AlertDialog.Builder(this)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장"){_,_ ->
                if(editText.text.isEmpty()){
                    showNameInputPopUp()
                } else{
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String) {
        val userId = getCurrentUserId()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)

        // 유저정보 가져와라
        getUnSelectedUsers()
    }

    private fun getCurrentUserId(): String {
        if(auth.currentUser == null){
            Toast.makeText(this, "로그인이 되어있지 않습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun like(){
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(DBKey.LIKED_BY)
            .child(DBKey.LIKE)
            .child(getCurrentUserId())
            .setValue(true)

        // 매칭이 된 시점을 추가해야함
        saveMatchIfOtherUserLikedMe(card.userId)

        Toast.makeText(this, "${card.name}님을 Like하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    // 내가 상대방에 like를 보내 놨는데 상대방이 나에게 like를 했을때 match되었다고 저장
    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        val otherUserDB = userDB.child(getCurrentUserId()).child(DBKey.LIKED_BY).child("like").child(otherUserId)

        otherUserDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value == true){
                    userDB.child(getCurrentUserId())
                        .child(DBKey.LIKED_BY)
                        .child("match")
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId)
                        .child(DBKey.LIKED_BY)
                        .child("match")
                        .child(getCurrentUserId())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun dislike(){
        val card = cardItems[manager.topPosition - 1]
        cardItems.removeFirst()

        userDB.child(card.userId)
            .child(DBKey.LIKED_BY)
            .child(DBKey.DISLIKE)
            .child(getCurrentUserId())
            .setValue(true)

        Toast.makeText(this, "${card.name}님을 disLike하셨습니다.", Toast.LENGTH_SHORT).show()
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {
        when(direction){
            Direction.Right -> like()
            Direction.Left -> dislike()
            else ->{}
        }
    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}


}