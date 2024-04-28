package com.example.grocery_admin.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grocery_admin.R
import com.example.grocery_admin.Utils
import com.example.grocery_admin.adapter.CartProductsAdapter
import com.example.grocery_admin.databinding.FragmentOrderDetailBinding
import com.example.grocery_admin.viewmodels.AdminViewModel
import kotlinx.coroutines.launch


class orderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var adapter: CartProductsAdapter
    private val viewModel : AdminViewModel by viewModels()
    private var status=0;
    private var currentStatus=0;
    private var orderId=""
    private var address=""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentOrderDetailBinding.inflate(inflater,container,false)
        getValues()
        onBackClicked()
        lifecycleScope.launch {
            getOrderedProducts()
        }
        settingStatus(status)
        ChangeStatusClicked()
        return binding.root
    }

    private fun ChangeStatusClicked() {
        binding.chnageStatus.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            popupMenu.menuInflater.inflate(R.menu.popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_received -> {
                        currentStatus=1
                        if (currentStatus>status){
                            status=1
                            settingStatus(1)
                            viewModel.updateOrderStatus(orderId, "Received")
                        }else{
                            Utils.showToast(requireContext(),"Item already Received......")
                        }
                        true
                    }
                    R.id.menu_dispatched -> {
                        currentStatus=2
                        if (currentStatus>status) {
                            status = 2
                            settingStatus(2)
                            viewModel.updateOrderStatus(orderId, "Dispatched")
                        }else{
                            Utils.showToast(requireContext(),"Item already Dispatched......")
                        }
                        true
                    }
                    R.id.menu_delivered -> {
                        currentStatus=3
                        if (currentStatus>status){
                            status=3
                            settingStatus(3)
                            viewModel.updateOrderStatus(orderId, "Delivered")
                        }else{
                            Utils.showToast(requireContext(),"Item already Delivered......")
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

    }


    private fun onBackClicked() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailFragment_to_orderFragment)
        }
    }
    private fun settingStatus(status : Int) {
        val statusToView = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1,binding.iv2,binding.view1),
            2 to listOf(binding.iv1,binding.iv2,binding.iv3,binding.view1,binding.view2),
            3 to listOf(binding.iv1,binding.iv2,binding.iv3,binding.iv4,binding.view1,binding.view2,binding.view3)
        )
        val viewToInt=statusToView.getOrDefault(status, emptyList())
        for (views in viewToInt){
            views.backgroundTintList= ContextCompat.getColorStateList(requireContext(),R.color.blue)
        }
    }
    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{cartList->
            adapter=CartProductsAdapter()
            binding.RVorderedItem.adapter=adapter
            adapter.differ.submitList(cartList)
        }
    }
    private fun getValues() {
        val bundles=arguments
        status=bundles!!.getInt("status")
        orderId= bundles.getString("orderId").toString()
        address= bundles.getString("address").toString()
        binding.address.text=address

    }


}