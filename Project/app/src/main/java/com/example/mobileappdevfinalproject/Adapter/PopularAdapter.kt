package com.example.mobileappdevfinalproject.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.mobileappdevfinalproject.databinding.ViewholderRecommendedBinding
import com.example.mobileappdevfinalproject.model.ItemModel

class PopularAdapter(
    private val items: MutableList<ItemModel>,
): RecyclerView.Adapter<PopularAdapter.ViewHolder>() {

    fun updateDate(newItems: MutableList<ItemModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ViewholderRecommendedBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ViewholderRecommendedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            titleTxt.text = item.title
            priceTxt.text = "$${item.price}"
            ratingTxt.text = item.rating.toString()
            Glide.with(holder.itemView.context)
                .load(item.picUrl.firstOrNull())
                .apply(RequestOptions().transform(CenterCrop()))
                .into(pic)
        }
    }

    override fun getItemCount(): Int = items.size
}
