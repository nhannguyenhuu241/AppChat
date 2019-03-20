package com.example.nhanne.chatmessage.messages

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nhanne.chatmessage.R
import com.example.nhanne.chatmessage.Views.ChatFromItem
import com.example.nhanne.chatmessage.Views.ChatToItem
import com.example.nhanne.chatmessage.models.ChatMessage
import com.example.nhanne.chatmessage.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter =  GroupAdapter<ViewHolder>()
    var toUser: User ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recycler_chatlog.adapter = adapter
        toUser =intent.getParcelableExtra<User>(NewMessageActivity.KEY)

        supportActionBar?.title = toUser?.username

        //setupDummyData()
        listenForMessage()

        send_button_chatlog.setOnClickListener {
            Log.d(TAG, "send ...")
            perfromSendMessage()
        }
    }

    private fun listenForMessage() {
        val Idfrom = FirebaseAuth.getInstance().uid
        val Idto = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$Idfrom/$Idto")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage !=null)
                {
                    if(chatMessage.idfrom == FirebaseAuth.getInstance().uid)
                    {
                        val currentUser = LastnestMessagesActivity.currentUser ?:return
                        adapter.add(ChatFromItem(chatMessage?.text, currentUser!!))
                    }
                    else
                    {

                        adapter.add(ChatToItem(chatMessage?.text,toUser!!))
                    }
                    Log.d(TAG, chatMessage?.text)
                    recycler_chatlog.scrollToPosition(adapter.itemCount -1)

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    private fun perfromSendMessage(){
        val text = edittext_chatlog.text.toString()
        val Idfrom = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.KEY)
        val Idto = user.uid
        if(Idfrom == null) return
        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$Idfrom/$Idto").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$Idto/$Idfrom").push()
        val chatMessage = ChatMessage(reference.key!!, text, Idfrom, Idto, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Save chat:${reference.key}")
            }
        toReference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Save chat:${reference.key}")
                edittext_chatlog.text.clear()
                recycler_chatlog.scrollToPosition(adapter.itemCount -1)
            }
        val lastnestMessageRef = FirebaseDatabase.getInstance().getReference("/lastest-messages/$Idfrom/$Idto")
        lastnestMessageRef.setValue(chatMessage)
        val lastnestMessageToRef = FirebaseDatabase.getInstance().getReference("/lastest-messages/$Idto/$Idfrom")
        lastnestMessageToRef.setValue(chatMessage)

    }
}

