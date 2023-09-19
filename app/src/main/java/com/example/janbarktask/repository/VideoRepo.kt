package com.example.janbarktask.repository

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.janbarktask.model.VideoItem
import java.io.File
import javax.inject.Inject

class VideoRepo @Inject constructor() {
    val _videoList = MutableLiveData<List<VideoItem>>()

    @SuppressLint("Range")
    fun loadVideos(activity: Activity): MutableLiveData<List<VideoItem>> {

        val videoList = mutableListOf<VideoItem>()
        val projection = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DURATION,
        )

        val cursor = activity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor!!.moveToNext()) {
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE))
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                val folder =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val size = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))
                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                val duration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                        .toLong()
                try {
                    val file = File(path)
                    val artUri = Uri.fromFile(file)
                    val videoItem = VideoItem(
                        title = title,
                        id = id,
                        folderName = folder,
                        duration = duration,
                        size = size,
                        path = path,
                        artUri = artUri
                    )
                    if (file.exists()) videoList.add(videoItem)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            cursor.close()
        }
        _videoList.postValue(videoList)
        return _videoList

    }


}