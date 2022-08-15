package com.ayan.imgeeditor

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class EditorLiveData (application: Application) : AndroidViewModel(application)  {
    val context: Context = application.applicationContext

    val LiveDataURI: MutableLiveData<Uri> = MutableLiveData()

}