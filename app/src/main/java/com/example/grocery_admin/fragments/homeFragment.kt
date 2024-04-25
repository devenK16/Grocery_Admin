package com.example.grocery_admin.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.grocery_admin.Constants
import com.example.grocery_admin.R
import com.example.grocery_admin.Utils
import com.example.grocery_admin.adapter.AdapterProduct
import com.example.grocery_admin.adapter.CategoriesAdapter
import com.example.grocery_admin.databinding.EditProductBinding
import com.example.grocery_admin.databinding.FragmentHomeBinding
import com.example.grocery_admin.models.Categories
import com.example.grocery_admin.models.Product
import com.example.grocery_admin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch


class homeFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterProduct: AdapterProduct

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        setStatusBarColor()
        setProductCategrory()
        searchProduct()
        getAllProducts("All")
        return binding.root
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun getAllProducts(title: String?) {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts(title).collect {
                if (it.isEmpty()) {
                    binding.textView2.visibility = View.VISIBLE
                    binding.productsRv.visibility = View.GONE
                } else {
                    binding.textView2.visibility = View.GONE
                    binding.productsRv.visibility = View.VISIBLE
                }
                adapterProduct = AdapterProduct(::onEditButtonClicked)
                binding.productsRv.adapter = adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = ArrayList(it)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }

    private fun searchProduct() {
        binding.searchet.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                adapterProduct.filter.filter(query)
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun setProductCategrory() {
        val categoryList = ArrayList<Categories>()
        for (i in 0 until Constants.allProductsCategory.size) {
            categoryList.add(
                Categories(
                    Constants.allProductsCategory[i],
                    Constants.allProductsCategoryIcon[i]
                )
            )
        }
        binding.cateoryRv.adapter = CategoriesAdapter(categoryList, ::getProductByCategory)
    }

    private fun getProductByCategory(category: Categories) {
        getAllProducts(category.title)
    }

    private fun onEditButtonClicked(product: Product) {
        val editProduct = EditProductBinding.inflate(LayoutInflater.from(requireContext()))
        editProduct.apply {
            etProductTitle.setText(product.productTitle)
            etproductQuantity.setText(product.productQuantity.toString())
            etProductunit.setText(product.productTUnit.toString())
            etPrice.setText(product.productPrice.toString())
            etNoOfStock.setText(product.productStock.toString())
            etProductCategory.setText(product.productCategory)
            etProductType.setText(product.productType)
        }

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(editProduct.root)
            .create()
        alertDialog.show()

        editProduct.edit.setOnClickListener {
            editProduct.etProductTitle.isEnabled = true
            editProduct.etproductQuantity.isEnabled = true
            editProduct.etProductunit.isEnabled = true
            editProduct.etPrice.isEnabled = true
            editProduct.etNoOfStock.isEnabled = true
            editProduct.etProductCategory.isEnabled = true
            editProduct.etProductType.isEnabled = true

            setAutoCompleteTextView(editProduct)
        }

        editProduct.save.setOnClickListener {
            lifecycleScope.launch {
                product.productTitle = editProduct.etProductTitle.text.toString()
                product.productQuantity = editProduct.etproductQuantity.text.toString().toInt()
                product.productTUnit = editProduct.etProductunit.text.toString()
                product.productPrice = editProduct.etPrice.text.toString().toInt()
                product.productStock = editProduct.etNoOfStock.text.toString().toInt()
                product.productCategory = editProduct.etProductCategory.text.toString()
                product.productType = editProduct.etProductType.text.toString()
                viewModel.saveingProducts(product)
            }

            Utils.showToast(requireContext(), "Changes Saved")
            alertDialog.dismiss()
        }
    }

    private fun setAutoCompleteTextView(editProduct: EditProductBinding) {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProduct)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)
        editProduct.apply {
            etProductunit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
    }
}

//    private fun searchProduct() {
//        binding.searchet.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val query=s.toString().trim()
//                adapterProduct.filter.filter(query)
//            }
//
//            override fun afterTextChanged(s: Editable?) {}
//
//        })
//    }


