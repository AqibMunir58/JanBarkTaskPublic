package com.example.janbarktask.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.janbarktask.adapter.VideoAdapter
import com.example.janbarktask.databinding.FragmentVideoBinding
import com.example.janbarktask.model.VideoItem
import com.example.janbarktask.util.RecyclerItemCLick
import com.example.janbarktask.viewmodel.VideoViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class VideoFragment : Fragment() {
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!

    private val videoViewModel: VideoViewModel by viewModels()
    private val TAG = "VideoFragment"
    private var mInterstitialAd: InterstitialAd? = null
    private lateinit var adRequest: AdRequest

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            if (it.value) {
                Toast.makeText(requireContext(), "Permision Granted", Toast.LENGTH_LONG).show()
                videoViewModel.getVideos(requireActivity()).observe(viewLifecycleOwner) {
                    setAdapter(it)
                }
            } else {
                Toast.makeText(requireContext(), "Permmision Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private val mGetPermision = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(requireContext(), "Permision Granted", Toast.LENGTH_LONG).show()

        } else {
            Toast.makeText(requireContext(), "Permision Denied", Toast.LENGTH_LONG).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(layoutInflater)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewvideos.layoutManager = linearLayoutManager
        binding.recyclerviewvideos.setHasFixedSize(true)
        adRequest = AdRequest.Builder().build()
        loadInterstitialAd()

        if (checkPermission()) {
            videoViewModel.getVideos(requireActivity()).observe(viewLifecycleOwner) {
                setAdapter(it)

            }

        } else {
            requestPermission()
        }
        return binding.root
    }

    private fun loadInterstitialAd() {
        InterstitialAd.load(
            requireContext(),
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, "onAdFailedToLoad: ${adError.message}")
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun setAdapter(it: List<VideoItem>?) {
        val adapter = VideoAdapter(requireActivity(), it!!, object : RecyclerItemCLick {
            override fun getItemClick(position: Int) {
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                    interstitialCallBack(it, position)
                    loadInterstitialAd()
                } else {
                    loadInterstitialAd()
                }

            }

        })
        binding.recyclerviewvideos.adapter = adapter
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.setData(Uri.parse(String.format("package:%s", requireContext().packageName)))
                mGetPermision.launch(intent)

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun interstitialCallBack(videoItems: List<VideoItem>, position: Int) {
        mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                mInterstitialAd = null
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("ArtUri", videoItems[position].artUri.toString())
                startActivity(intent)

            }


            override fun onAdImpression() {
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }


}