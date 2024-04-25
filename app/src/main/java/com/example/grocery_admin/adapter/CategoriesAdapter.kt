package com.example.grocery_admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery_admin.databinding.ItemViewProductCategoriesBinding
import com.example.grocery_admin.models.Categories

class CategoriesAdapter(private val categoryList: ArrayList<Categories>, val getProductByCategory: (Categories) -> Unit,) :
    RecyclerView.Adapter<CategoriesAdapter.viewHolder>() {


    class viewHolder(val binding : ItemViewProductCategoriesBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(ItemViewProductCategoriesBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val categoryList=categoryList[position]
        holder.binding.apply {
            CategoriesImage.setImageResource(categoryList.image)
            CategoriesTitle.text=categoryList.title
        }
        holder.itemView.setOnClickListener {
            getProductByCategory(categoryList)
        }
    }
}