package com.example.janbarktask.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.janbarktask.R
import com.example.janbarktask.databinding.ItemVideoBinding
import com.example.janbarktask.databinding.NativeLayoutBinding
import com.example.janbarktask.model.VideoItem
import com.example.janbarktask.util.RecyclerItemCLick
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView


class VideoAdapter(
    val activity: Activity,
    val videoList: List<VideoItem>,
    val listener: RecyclerItemCLick
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(
            activity
        )
        if (viewType == ITEM_VIEW) {
            val view: View =
                layoutInflater.inflate(R.layout.item_video, parent, false)
           return  MainViewHolder(view)
        } else {
            val view: View = layoutInflater.inflate(R.layout.native_layout, parent, false)
            return AdViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_VIEW) {
            (holder as MainViewHolder).bindData(videoList[position])
            holder.itemView.setOnClickListener {
                listener.getItemClick(position)
            }
        } else {
            (holder as AdViewHolder).bindAdData()
        }
    }


    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position > 1 && (position + 1) % 4 == 0) {

            return AD_VIEW;
        } else {

            return ITEM_VIEW;
        }
    }

    private fun populateNativeADView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        adView.headlineView = adView.findViewById<View>(R.id.ad_headline)
        adView.bodyView = adView.findViewById<View>(R.id.ad_body)
        adView.callToActionView = adView.findViewById<View>(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById<View>(R.id.ad_app_icon)
        adView.priceView = adView.findViewById<View>(R.id.ad_price)
        adView.starRatingView = adView.findViewById<View>(R.id.ad_stars)
        adView.storeView = adView.findViewById<View>(R.id.ad_store)
        adView.advertiserView = adView.findViewById<View>(R.id.ad_advertiser)
        (adView.headlineView as TextView?)!!.text = nativeAd.headline
        adView.mediaView!!.mediaContent = nativeAd.mediaContent
        if (nativeAd.body == null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView?)!!.text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button?)!!.text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView!!.visibility = View.GONE
        } else {
            (adView.iconView as ImageView?)!!.setImageDrawable(
                nativeAd.icon!!.drawable
            )
            adView.iconView!!.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView?)!!.text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView?)!!.text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar?)!!.rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView?)!!.text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    inner class MainViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView!!) {
        var binding: ItemVideoBinding

        init {
            binding = ItemVideoBinding.bind(itemView)
        }

        fun bindData(videoItem: VideoItem) {
            binding.videoName.text = videoItem.title
            binding.size.text = videoItem.size
            binding.duration.text =
                DateUtils.formatElapsedTime(videoItem.duration / 1000)
            Glide.with(itemView.context).asBitmap().load(videoItem.artUri)
                .into(binding.videoImg)
        }
    }

    inner class AdViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var binding: NativeLayoutBinding

        init {
            binding = NativeLayoutBinding.bind(itemView)
        }

        @SuppressLint("InflateParams")
        fun bindAdData() {
            val builder = AdLoader.Builder(
                activity, "ca-app-pub-3940256099942544/2247696110"
            )
                .forNativeAd { nativeAd ->
                    val nativeAdView = activity.layoutInflater.inflate(
                        R.layout.layout_native_ad,
                        null
                    ) as NativeAdView
                    populateNativeADView(nativeAd, nativeAdView)
                    binding.adLayout.removeAllViews()
                    binding.adLayout.addView(nativeAdView)
                }
            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Toast.makeText(activity, loadAdError.message, Toast.LENGTH_SHORT).show()
                }
            }).build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    companion object {
        private const val ITEM_VIEW = 0
        private const val AD_VIEW = 1

    }
}


/*
class VideoAdapter(
    val videoList: List<VideoItem>,
    val listener: RecyclerItemCLick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW = 0
    private val AD_VIEW = 1
     var activity1 : Activity? = null
    fun setContent(activity : Activity)
    {
        activity1 = activity

    }

    class ViewHolder(val binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {}
    class AdViewHolder(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    ) {
        var binding: NativeLayoutBinding

        init {
            binding = NativeLayoutBinding.bind(itemView!!)
        }

        private fun bindAdData() {
            val builder = AdLoader.Builder(activity1, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd { nativeAd ->
                    val nativeAdView = activity.getLayoutInflater().inflate(
                        R.layout.layout_native_ad,
                        null
                    ) as NativeAdView
                    populateNativeADView(nativeAd, nativeAdView)
                    binding.adLayout.removeAllViews()
                    binding.adLayout.addView(nativeAdView)
                }
            val adLoader = builder.withAdListener(object : AdListener() {
                fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Toast.makeText(activity, loadAdError.getMessage(), Toast.LENGTH_SHORT).show()
                }
            }).build()
            adLoader.loadAd(Builder().build())
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_VIEW) {
            val binding =
                ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        } else {

        }

//       if (viewType == AD_TYPE)
//       {
//           return adViewHolder(
//               LayoutInflater.from(parent.context).inflate(R.layout.native_layout, null, false)
//           )
//       }
//        else
//       {
//           val binding =
//               ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//           return ViewHolder(binding)
//       }
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        view = R.layout.native_layout
        if (getItemViewType(position) == CONTENT_TYPE) {

            (holder as ViewHolder).binding.videoName.text = videoList[position].title
            holder.binding.size.text = videoList[position].size
            holder.binding.duration.text =
                DateUtils.formatElapsedTime(videoList[position].duration / 1000)
            Glide.with(holder.itemView.context).asBitmap().load(videoList[position].artUri)
                .into(holder.binding.videoImg)
            holder.itemView.setOnClickListener {
                listener.getItemClick(position)
            }
        } else {
            val adLoader2 =
                AdLoader.Builder(holder.itemView.context, "ca-app-pub-3940256099942544/2247696110")
                    .forNativeAd {
                        NativeAd.OnNativeAdLoadedListener {
                            val styles =
                                NativeTemplateStyle.Builder()
                                    .build()
                            val template: TemplateView = view
                            template.setStyles(styles)
                            template.setNativeAd(nativeAd)
                        }
                    }


        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position > 1 && (position + 1) % 4 == 0) {

            return AD_TYPE;
        } else {

            return CONTENT_TYPE;
        }
    }

    private fun populateNativeADView(nativeAd: NativeAd, adView: NativeAdView) {

        adView.setMediaView(adView.findViewById(R.id.ad_media))

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline))
        adView.setBodyView(adView.findViewById(R.id.ad_body))
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action))
        adView.setIconView(adView.findViewById(R.id.ad_app_icon))
        adView.setPriceView(adView.findViewById(R.id.ad_price))
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars))
        adView.setStoreView(adView.findViewById(R.id.ad_store))
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser))

        (adView.getHeadlineView() as TextView).setText(nativeAd.headline)
        adView.getMediaView()!!.setMediaContent(nativeAd.mediaContent)

        if (nativeAd.body == null) {
            (adView.getBodyView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getBodyView() as TextView).setVisibility(View.VISIBLE)
            (adView.getBodyView() as TextView).setText(nativeAd.body)
        }
        if (nativeAd.callToAction == null) {
            (adView.getCallToActionView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getCallToActionView() as TextView).setVisibility(View.VISIBLE)
            (adView.getCallToActionView() as Button).setText(nativeAd.callToAction)
        }
        if (nativeAd.icon == null) {
            (adView.getIconView() as TextView).setVisibility(View.GONE)
        } else {
            (adView.getIconView() as ImageView).setImageDrawable(
                nativeAd.icon!!.drawable
            )
            (adView.getIconView() as Any).setVisibility(View.VISIBLE)
        }
        if (nativeAd.price == null) {
            (adView.getPriceView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getPriceView() as TextView).setVisibility(View.VISIBLE)
            (adView.getPriceView() as TextView).setText(nativeAd.price)
        }
        if (nativeAd.store == null) {
            (adView.getStoreView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getStoreView() as TextView).setVisibility(View.VISIBLE)
            (adView.getStoreView() as TextView).setText(nativeAd.store)
        }
        if (nativeAd.starRating == null) {
            (adView.getStarRatingView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getStarRatingView() as RatingBar).setRating(nativeAd.starRating!!.toFloat())

        }
        if (nativeAd.advertiser == null) {
            (adView.getAdvertiserView() as TextView).setVisibility(View.INVISIBLE)
        } else {
            (adView.getAdvertiserView() as TextView).setText(nativeAd.advertiser)
            (adView.getAdvertiserView() as TextView).setVisibility(View.VISIBLE)
        }

        adView.setNativeAd(nativeAd)
    }

    */
/*   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
           if (viewType == AD_TYPE) {
               return adViewHolder(
                   LayoutInflater.from(parent.context).inflate(R.layout.item_ads, null, false)
               )
           }
           else
           {
               val binding =
                   ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
               return ViewHolder(binding)
           }


       }

       override fun getItemCount(): Int {
           return videoList.size
       }

       override fun onBindViewHolder(holder: ViewHolder, position: Int) {
           holder.binding.videoName.text = videoList[position].title
           holder.binding.size.text = videoList[position].size
           holder.binding.duration.text = DateUtils.formatElapsedTime(videoList[position].duration/1000)
          Glide.with(holder.itemView.context).asBitmap().load(videoList[position].artUri).into(holder.binding.videoImg)
           holder.itemView.setOnClickListener {
               listener.getItemClick(position)
           }

       }*//*

}*/
