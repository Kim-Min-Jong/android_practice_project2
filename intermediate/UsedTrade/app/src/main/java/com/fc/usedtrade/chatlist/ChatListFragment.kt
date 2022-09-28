package com.fc.usedtrade.chatlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.fc.usedtrade.R
import com.fc.usedtrade.databinding.FragmentChatListBinding
import com.fc.usedtrade.util.DBKey.Companion.CHILD_CHAT
import com.fc.usedtrade.util.DBKey.Companion.DB_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    private var binding: FragmentChatListBinding? = null
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatListItem>()
    private lateinit var chatDB: DatabaseReference
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatListBinding.bind(view)

        chatListAdapter = ChatListAdapter {
            // 채팅방으로 이동하는 로직
        }

        chatRoomList.clear()
        binding?.chatListRecyclerView?.adapter = chatListAdapter
        binding?.chatListRecyclerView?.layoutManager = LinearLayoutManager(context)

        if(auth.currentUser == null) {
            return
        }

        chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser?.uid!!).child(CHILD_CHAT)

        // 채팅방 가져와서 초기화 (프래그먼트에 바인딩)
        chatDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}