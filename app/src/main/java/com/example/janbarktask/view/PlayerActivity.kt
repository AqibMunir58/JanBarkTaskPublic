package com.example.janbarktask.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import com.example.janbarktask.R
import com.example.janbarktask.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

class PlayerActivity : AppCompatActivity() {
    private var _binding : ActivityPlayerBinding? = null
    val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val ArtUri  = intent.getStringExtra("ArtUri")
        val uri : Uri =Uri.parse(ArtUri)
        val player = SimpleExoPlayer.Builder(this).build()
        binding.playerView.player = player
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.prepare()

        player.stop()
    }
}