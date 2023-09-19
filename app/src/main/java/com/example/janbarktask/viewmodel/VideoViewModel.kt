package com.example.janbarktask.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.janbarktask.model.VideoItem
import com.example.janbarktask.repository.VideoRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VideoViewModel @Inject constructor(private val videoRepo: VideoRepo) : ViewModel() {
    var videoList: MutableLiveData<List<VideoItem>>? = null
    fun getVideos(activity: Activity): LiveData<List<VideoItem>> {
        videoList = videoRepo.loadVideos(activity)
        return videoList!!
    }


}


