package com.example.mobileappdevfinalproject.Adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappdevfinalproject.databinding.ViewholderColorBinding

class ColorAdapter(private val items: ArrayList<String>): RecyclerView.Adapter<ColorAdapter.ViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1
    inner class ViewHolder(val binding: ViewholderColorBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): ViewHolder {
        val binding = ViewholderColorBinding.inflate(
            LayoutInflater.from(p0.context), p0, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
      val color=items[p1].toColorInt()
        p0.binding.colorCircle.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        p0.binding.strokeView.visibility=if (p1 == selectedPosition)
            View.VISIBLE
        else
            View.GONE
        p0.binding.root.setOnClickListener {
            if(selectedPosition != p1){
                lastSelectedPosition=selectedPosition
                selectedPosition=p1
               if(lastSelectedPosition != -1) notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}