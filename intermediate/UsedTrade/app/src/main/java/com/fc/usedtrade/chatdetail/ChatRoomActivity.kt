package com.fc.usedtrade.chatdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.usedtrade.databinding.ActivityChatRoomBinding
import com.fc.usedtrade.util.DBKey.Companion.DB_CHAT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {
    private var binding: ActivityChatRoomBinding? = null
    private var chatDB: DatabaseReference? = null
    private val adapter = ChatItemAdapter()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val chatList = mutableListOf<ChatItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val chatKey = intent.getLongExtra("chatKey", -1)

        chatDB = Firebase.database.reference.child(DB_CHAT).child("$chatKey")
        chatDB?.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // chatItme 형식으로 가져옴 snapshot도 같은 json 형식
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })

        binding?.chatRoomRecyclerView?.adapter = adapter
        binding?.chatRoomRecyclerView?.layoutManager = LinearLayoutManager(this)

        binding?.sendButton?.setOnClickListener {
            val chatItem = ChatItem(
                auth.currentUser?.uid!!,
                binding?.messageEditText?.text.toString()
            )

            chatDB?.push()?.setValue(chatItem)
            binding?.messageEditText?.text?.clear()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}