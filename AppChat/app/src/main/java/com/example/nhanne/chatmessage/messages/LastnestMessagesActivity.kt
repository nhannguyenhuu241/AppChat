package com.example.nhanne.chatmessage.messages

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.nhanne.chatmessage.R
import com.example.nhanne.chatmessage.Views.LastnestMessageRow
import com.example.nhanne.chatmessage.models.ChatMessage
import com.example.nhanne.chatmessage.models.User
import com.example.nhanne.chatmessage.registerlogin.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_lastnest_messages.*

class LastnestMessagesActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
        val TAG = "LatestMessages"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lastnest_messages)

        recyclerview_latest_messages.adapter =  adapter
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        //setupDummyRows()
        adapter.setOnItemClickListener { item, view ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)

            val row = item as LastnestMessageRow
            intent.putExtra(NewMessageActivity.KEY, row.chatPartnerUser)
            startActivity(intent)
        }
        listenForLastestMessage()
        fetchCurrentUser()
        verifyUserIsLoggedIn()
        }

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecycleViewMessage(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LastnestMessageRow(it))
        }
    }
    private fun listenForLastestMessage() {
        val Idfrom = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-message/$Idfrom")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return

                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecycleViewMessage()
                //adapter.add(LastnestMessageRow(chatMessage))
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)?:return
                //adapter.add(LastnestMessageRow(chatMessage))
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecycleViewMessage()
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    val adapter = GroupAdapter<ViewHolder>()


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyUserIsLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
