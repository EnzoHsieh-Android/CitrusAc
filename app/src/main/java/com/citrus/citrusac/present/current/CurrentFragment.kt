package com.citrus.citrusac.present.current

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
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
import com.citrus.util.ext.showErrDialog
import com.citrus.util.ext.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class CurrentFragment : BaseFragment(R.layout.fragment_current) {
    private val binding by viewBinding(FragmentCurrentBinding::bind)
    private val viewModel: CurrentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    var scope = CoroutineScope(Job() + Dispatchers.Main)

    @Inject
    lateinit var currentAcAdapter: CurrentAcAdapter

    companion object {
        fun newInstance(): CurrentFragment {
            return CurrentFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.startFetchJob()
        sharedViewModel.setViewPagerSwitch(PageType.Current)
    }


    override fun initView() {
        binding.apply {
            binding.resultSuccess.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    scope.launch {
                        delay(500)
                        binding.successAnimation.visibility = View.GONE
                    }
                }
            })

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

        lifecycleFlow(viewModel.acSerialError) {
            viewModel.stopFetchJob()
            showErrDialog(
                title = "與系統連線發生問題",
                msg = it,
                onConfirmListener = {
                    viewModel.startFetchJob()
                }
            )
        }

        lifecycleFlow(sharedViewModel.setAcDataSuccess) {
            when (it) {
                Constants.SUCCESS -> {
                    binding.resultSuccess.setAnimation("success.json")
                    binding.successAnimation.visibility = View.VISIBLE
                    binding.resultSuccess.playAnimation()
                    binding.resultHint.text = "補登成功！"
                }

                Constants.ERROR -> {
                    binding.resultSuccess.setAnimation("error.json")
                    binding.successAnimation.visibility = View.VISIBLE
                    binding.resultSuccess.playAnimation()
                    binding.resultHint.text = "查無會員資料"
                }
            }
        }

        lifecycleFlow(viewModel.acLatest) {
            when (it) {
                is Resource.Loading -> Unit

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

    @SuppressLint("NotifyDataSetChanged")
    override fun initAction() {
        currentAcAdapter.setOnClickListener {
            updateInfoDetail(it)
            currentAcAdapter.notifyDataSetChanged()
        }
    }


    @SuppressLint("SetTextI18n")
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


    override fun onDestroyView() {
        scope.cancel()
        super.onDestroyView()
    }

    override fun onPause() {
        viewModel.stopFetchJob()
        super.onPause()
    }


}