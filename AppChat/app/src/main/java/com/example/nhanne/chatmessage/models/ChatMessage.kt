package com.example.nhanne.chatmessage.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatMessage(val id : String, val text : String, val idfrom : String, val idto : String, val timestamp :Long):
    Parcelable{
    constructor():this("","","","",-1)
}