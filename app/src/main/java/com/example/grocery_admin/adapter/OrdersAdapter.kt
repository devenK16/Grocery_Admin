package com.example.grocery_admin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery_admin.R
import com.example.grocery_admin.databinding.SampleOrderBinding
import com.example.grocery_admin.models.OrderedItems

class OrdersAdapter(val context: Context, val showOrderDetail: (OrderedItems) -> Unit) :
    RecyclerView.Adapter<OrdersAdapter.viewHolder>() {
    class viewHolder(val binding: SampleOrderBinding) : RecyclerView.ViewHolder(binding.root) {}


    val diffUtil = object : DiffUtil.ItemCallback<OrderedItems>() {
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            SampleOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentOrder: OrderedItems = differ.currentList[position]
        holder.binding.apply {
            title.text = currentOrder.itemTitle
            price.text = "₹${currentOrder.itemPrice.toString()}"
            date.text = currentOrder.itemDate
            when (currentOrder.itemStatus) {
                0 -> {
                    status.text = "Ordered"
                    status.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.yellow)
                }

                1 -> {
                    status.text = "Received"
                    status.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.blue)
                }

                2 -> {
                    status.text = "Dispatched"
                    status.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.green)
                }

                3 -> {
                    status.text = "Delivered"
                    status.backgroundTintList =
                        ContextCompat.getColorStateList(context, R.color.orange)
                }
            }
        }
        holder.itemView.setOnClickListener {
            showOrderDetail(currentOrder)
        }
    }
}