package com.example.mobileappdevfinalproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.loader.R
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mobileappdevfinalproject.databinding.ViewholderPicsBinding

class PicsAdapter(val items: MutableList<String>, private val onImageSelected: (String) -> Unit) :
    RecyclerView.Adapter<PicsAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    private lateinit var context: Context

    inner class ViewHolder(val binding: ViewholderPicsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PicsAdapter.ViewHolder {
        context = parent.context
        val binding = ViewholderPicsBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PicsAdapter.ViewHolder, position: Int) {
        Glide.with(context)
            .load(items[position])
            .into(holder.binding.pic)

        holder.binding.root.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                lastSelectedPosition = selectedPosition
                selectedPosition = currentPosition

                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)

                onImageSelected(items[currentPosition])
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
