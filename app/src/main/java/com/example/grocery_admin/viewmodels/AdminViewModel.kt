package com.example.grocery_admin.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.grocery_admin.Utils
import com.example.grocery_admin.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class AdminViewModel : ViewModel() {

    private val _isImageUploaded = MutableStateFlow(false)
    val isImageUploaded = _isImageUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    val downloadedUrls = _downloadedUrls

    private val _isProductSaved = MutableStateFlow(false)
    val isProductSaved = _isProductSaved

    fun saveImagesInDB(imageUri: ArrayList<Uri>) {
        val downloadUrls = ArrayList<String?>()

        imageUri.forEach { uri ->
            val imageRef =
                FirebaseStorage.getInstance().reference.child(Utils.getCurrentUid()).child("images")
                    .child(UUID.randomUUID().toString())
            imageRef.putFile(uri).continueWithTask {
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                val url = task.result
                downloadUrls.add(url.toString())

                if (downloadUrls.size == imageUri.size) {
                    _isImageUploaded.value = true
                    _downloadedUrls.value = downloadUrls
                }
            }
        }
    }

    fun saveProduct(product: Product) {
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
            .child(product.productRandomId).setValue(product)
            .addOnSuccessListener {
                FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory")
                    .child(product.productCategory!!).child(product.productRandomId)
                    .setValue(product)
                    .addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType")
                            .child(product.productType!!).child(product.productRandomId)
                            .setValue(product)
                            .addOnSuccessListener {
                                _isProductSaved.value = true
                            }

                    }
            }
    }

    fun fetchAllProducts(title: String?): Flow<List<Product>> = callbackFlow {
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (title == "All" || prod?.productCategory == title) {
                        products.add(prod!!)
                    }
                }
                val sortedProducts = products.sortedByDescending { it.timestamp }
                trySend(sortedProducts)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose { db.removeEventListener(eventListener) }
    }

    fun saveingProducts(product: Product){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts").child(product.productRandomId).setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory").child(product.productCategory!!).child(product.productRandomId).setValue(product)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductType").child(product.productType!!).child(product.productRandomId).setValue(product)
    }
}