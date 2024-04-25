package com.example.grocery_admin.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocery_admin.databinding.SampleSelectedImageBinding

class AdapterSelectedImage(val imageUri: ArrayList<Uri>) :
    RecyclerView.Adapter<AdapterSelectedImage.SelectedImageViewHolder>() {
    class SelectedImageViewHolder(val binding: SampleSelectedImageBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedImageViewHolder {
        return SelectedImageViewHolder(
            SampleSelectedImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return imageUri.size
    }

    override fun onBindViewHolder(holder: SelectedImageViewHolder, position: Int) {
        val images = imageUri[position]
        holder.binding.apply {
            image.setImageURI(images)
        }
        holder.binding.remove.setOnClickListener {
            if (position < imageUri.size) {
                imageUri.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}