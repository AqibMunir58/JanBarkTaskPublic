package com.example.janbarktask.model

import android.net.Uri

data class VideoItem(
    val id: String,
    val title: String,
    val duration: Long = 0,
    val folderName: String,
    val size: String,
    val path: String,
    val artUri: Uri
)
