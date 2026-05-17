package com.example.mobileappdevfinalproject.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.mobileappdevfinalproject.R
import com.example.mobileappdevfinalproject.model.SliderModel

class SliderAdapter (
    private val sliderItems: List<SliderModel>,
    private val viewPager2: ViewPager2
    ) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageSlide)

        fun setImage(sliderItem: SliderModel, context: Context) {
            // Check for multiple possible field names (url, picUrl, image)
            val imageUrl = when {
                sliderItem.url.isNotEmpty() -> sliderItem.url
                sliderItem.picUrl.isNotEmpty() -> sliderItem.picUrl
                sliderItem.image.isNotEmpty() -> sliderItem.image
                else -> ""
            }

            Log.d("SliderAdapter", "Loading image: $imageUrl")

            val requestOption = RequestOptions().transform(CenterCrop())

            Glide.with(context)
                .load(imageUrl)
                .apply(requestOption)
                .placeholder(R.drawable.grey_full_corner) // Optional: add a placeholder
                .error(android.R.drawable.stat_notify_error) // Optional: show error icon if load fails
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item_container, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(sliderItems[position], holder.itemView.context)
    }

    override fun getItemCount(): Int = sliderItems.size
}
