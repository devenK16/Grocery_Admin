package com.example.grocery_admin.fragments

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.grocery_admin.AdminMainActivity
import com.example.grocery_admin.Constants
import com.example.grocery_admin.R
import com.example.grocery_admin.Utils
import com.example.grocery_admin.adapter.AdapterSelectedImage
import com.example.grocery_admin.databinding.FragmentAddProductBinding
import com.example.grocery_admin.models.Product
import com.example.grocery_admin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [addProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class addProductFragment : Fragment() {
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var binding: FragmentAddProductBinding
    private val ImageUri: ArrayList<Uri> = arrayListOf()
    val selectedImage =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { listOfUri ->
            val fiveImages = listOfUri.take(5)
            ImageUri.clear()
            ImageUri.addAll(fiveImages)
            binding.selectedImageRv.adapter = AdapterSelectedImage(ImageUri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        setStatusBarColor()
        setAutoCompleteTextViews()
        onImageButtonClick()
        onAddbtnClicked()
        return binding.root
    }

    private fun onAddbtnClicked() {
        binding.btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading Images")
            val productTitle = binding.etProductTitle.text.toString()
            val productQuantity = binding.etproductQuantity.text.toString()
            val productUnit = binding.etProductunit.text.toString()
            val productPrice = binding.etPrice.text.toString()
            val productStock = binding.etNoOfStock.text.toString()
            val productCategory = binding.etProductCategory.text.toString()
            val productType = binding.etProductType.text.toString()

            if (productTitle.isEmpty() || productQuantity.isEmpty() || productUnit.isEmpty() ||
                productStock.isEmpty() || productPrice.isEmpty() || productCategory.isEmpty() || productType.isEmpty()
            ) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Empty Fields are not allowed")
                return@setOnClickListener
            } else if (ImageUri.isEmpty()) {
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Please select some Images")
                return@setOnClickListener
            } else {
                val products = Product(
                    productTitle = productTitle,
                    productQuantity = productQuantity.toInt(),
                    productTUnit = productUnit,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productCategory = productCategory,
                    productType = productType,
                    itemCount = 0,
                    adminUid = Utils.getCurrentUid()
                )

                saveImage(products)
            }
        }
    }

    private fun saveImage(products: Product) {
        viewModel.saveImagesInDB(ImageUri)
        lifecycleScope.launch {
            viewModel.isImageUploaded.collect {
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Image Uploaded")
                    getUrls(products)
                }
            }
        }
    }

    private fun getUrls(products: Product) {
        Utils.showDialog(requireContext(), "Publishing Product........")
        lifecycleScope.launch {
            viewModel.downloadedUrls.collect {
                val urls = it
                products.productImageUris = urls
                saveProduct(products)
            }
        }
    }

    private fun saveProduct(products: Product) {
        viewModel.saveProduct(products)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect{
                if (it){
                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(),"Your Product is Live")
                }
            }
        }
    }

    private fun onImageButtonClick() {
        binding.btnSelectedimage.setOnClickListener {
            selectedImage.launch("image/*")
        }
    }

    private fun setAutoCompleteTextViews() {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnitsOfProduct)
        val category =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductsCategory)
        val productType =
            ArrayAdapter(requireContext(), R.layout.show_list, Constants.allProductType)
        binding.apply {
            etProductunit.setAdapter(units)
            etProductCategory.setAdapter(category)
            etProductType.setAdapter(productType)
        }
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


}