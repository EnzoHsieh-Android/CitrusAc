package com.citrus.citrusac.present.current

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.VideoDecoder.asset
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.FragmentCurrentBinding
import com.citrus.citrusac.present.current.adapter.CurrentAcAdapter
import com.citrus.citrusac.present.main.PageType
import com.citrus.citrusac.present.main.SharedViewModel
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants
import com.citrus.util.Constants.getFromServerTime
import com.citrus.util.Constants.getResServerTime
import com.citrus.util.Constants.setChips
import com.citrus.util.base.BaseFragment
import com.citrus.util.ext.lifecycleFlow
import com.citrus.util.ext.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import splitties.views.onClick
import javax.inject.Inject

@AndroidEntryPoint
class CurrentFragment : BaseFragment(R.layout.fragment_current) {
    private val binding by viewBinding(FragmentCurrentBinding::bind)
    private val viewModel: CurrentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var currentAcAdapter: CurrentAcAdapter

    companion object {
        fun newInstance(): CurrentFragment {
            return CurrentFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setViewPagerSwitch(PageType.Current)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init()
    }

    override fun initView() {
        binding.apply {
            binding.resultSuccess.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    MainScope().launch {
                        binding.successAnimation.visibility = View.GONE
                    }
                }
            })

            llInfo.onClick {


            }

            rvCurrent.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = currentAcAdapter
            }
        }
    }

    override fun initObserve() {
        lifecycleFlow(viewModel.acSerial) {
            if (it.isNotBlank()) {
                viewModel.getRecordLatest()
            }
        }

        lifecycleFlow(sharedViewModel.setAcDataSuccess) {
            when (it) {
                Constants.SUCCESS -> {
                    binding.resultSuccess.setAnimation("success.json")
                    binding.successAnimation.visibility = View.VISIBLE
                    binding.resultSuccess.playAnimation()
                }

                Constants.ERROR -> {
                    binding.resultSuccess.setAnimation("fail.json")
                    binding.successAnimation.visibility = View.VISIBLE
                    binding.resultSuccess.playAnimation()
                }
            }
        }

        lifecycleFlow(viewModel.acLatest) {
            when (it) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    binding.rvCurrent.isVisible = true
                    binding.noneInfo.isVisible = false
                    binding.noneInfo2.isVisible = false

                    it.data.first().selected = true
                    currentAcAdapter.updateDataset(it.data.take(4) as MutableList<AccessLatest>)

                    updateInfoDetail(it.data.first())
                }

                is Resource.Error -> {
                    binding.rvCurrent.isVisible = false
                    binding.noneInfo.isVisible = true
                    binding.noneInfo2.isVisible = true
                }
            }
        }

    }

    override fun initAction() {
        currentAcAdapter.setOnClickListener {
            updateInfoDetail(it)
            currentAcAdapter.notifyDataSetChanged()
        }
    }


    private fun updateInfoDetail(info: AccessLatest) {
        binding.apply {
            noneInfo.visibility = View.GONE

            Glide.with(root)
                .load(info.photoPath)
                .placeholder(R.drawable.default_user)
                .into(avatar)

            tvName.text = info.name
            tvCustNo.text = info.custNo
            tvBirthday.text = getFromServerTime(info.birth)

            if (info.notes.isNotEmpty()) {
                setChips(
                    requireContext(),
                    info.notes.map { it.note },
                    binding.memoChip,
                    binding.tvName.textSize
                )
                binding.hashTag.isVisible = true
                binding.llMemoChip.isVisible = true
            } else {
                binding.hashTag.isVisible = false
                binding.llMemoChip.isVisible = false
            }

            if (info.resData.isNotEmpty()) {
                llResInfo.visibility = View.VISIBLE
                resTime.text = getResServerTime(info.resData[0].resTime)
                resNum.text = info.resData[0].custNum.toString() + "人"
                if (info.resData[0].memo.isNotBlank()) {
                    resMemo.text = info.resData[0].memo
                    resMemo.visibility = View.VISIBLE
                } else {
                    resMemo.visibility = View.GONE
                }
            } else {
                llResInfo.visibility = View.GONE
            }

            llDetailInfo.isVisible = true
        }
    }


}