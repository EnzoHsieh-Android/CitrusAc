package com.citrus.citrusac.present.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.FragmentCurrentBinding
import com.citrus.citrusac.databinding.FragmentMainBinding
import com.citrus.citrusac.present.current.CurrentFragment
import com.citrus.citrusac.present.history.HistoryFragment
import com.citrus.util.base.BaseFragment
import com.citrus.util.ext.lifecycleFlow
import com.citrus.util.ext.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.onClick

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {
    private val binding by viewBinding(FragmentMainBinding::bind)
    private var collectionAdapter: CollectionAdapter? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun initView() {
        collectionAdapter = CollectionAdapter(this)
        binding.apply {
            viewPager.offscreenPageLimit = 2
            viewPager.adapter = collectionAdapter
        }


    }

    override fun initObserve() {
        lifecycleFlow(sharedViewModel.setPageType) {
            when (it) {
                is PageType.Current -> {
                    binding.viewPager.currentItem = 0
                }
                is PageType.History -> {
                    binding.viewPager.currentItem = 1
                }
            }
        }

    }

    override fun initAction() {

    }


    inner class CollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CurrentFragment.newInstance()
                1 -> HistoryFragment.newInstance()
                else -> CurrentFragment.newInstance()
            }
        }

    }
}