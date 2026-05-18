package com.example.mobileappdevfinalproject.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.mobileappdevfinalproject.Cart.ChangeNumberItemsListener
import com.example.mobileappdevfinalproject.Cart.ManagementCart
import com.example.mobileappdevfinalproject.databinding.ViewholderCartBinding
import com.example.mobileappdevfinalproject.model.ItemModel

class CartAdapter(private  val listItemSelected: ArrayList<ItemModel>
, context: Context,
    var changeNumberItemsListener: ChangeNumberItemsListener? = null
): RecyclerView.Adapter<CartAdapter.Viewholder>() {
    private val managementCart= ManagementCart(context)
    class Viewholder(val binding: ViewholderCartBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): Viewholder {
      val binding= ViewholderCartBinding.inflate(LayoutInflater.from(p0.context),p0,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(p0: CartAdapter.Viewholder, p1: Int) {
        val item=listItemSelected[p1]
        p0.binding.titleTxt.text=item.title
        p0.binding.feeEachItem.text="$${item.price}"
        p0.binding.totalEachItem.text="$${item.price * item.numberInCart}"
        p0.binding.numberItemTxt.text=item.numberInCart.toString()

        Glide.with(p0.itemView.context)
            .load(item.picUrl[0])
            .apply(RequestOptions().transform(CenterCrop()))
            .into(p0.binding.pic)

        p0.binding.plusCartBtn.setOnClickListener {
            managementCart.plusItem(listItemSelected, p1,object : ChangeNumberItemsListener {
                override fun onChanged() {
                        notifyDataSetChanged()
                        changeNumberItemsListener?.onChanged()
                }


            })
        }
        p0.binding.minusCartBtn.setOnClickListener {
            managementCart.minusItem(listItemSelected, p1, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    notifyDataSetChanged()
                    changeNumberItemsListener?.onChanged()
                }
            })
        }
    }

    override fun getItemCount(): Int = listItemSelected.size
}