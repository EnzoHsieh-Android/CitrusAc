package com.citrus.citrusac.present.current

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.FragmentCurrentBinding
import com.citrus.citrusac.present.current.adapter.CurrentAcAdapter
import com.citrus.citrusac.present.current.adapter.MemoAdapter
import com.citrus.citrusac.present.current.adapter.ResAdapter
import com.citrus.citrusac.present.main.PageType
import com.citrus.citrusac.present.main.SharedViewModel
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessLatest
import com.citrus.remote.vo.Reservation
import com.citrus.util.Constants
import com.citrus.util.Constants.getFromServerTime
import com.citrus.util.Constants.getResServerTime
import com.citrus.util.base.BaseFragment
import com.citrus.util.ext.lifecycleFlow
import com.citrus.util.ext.showErrDialog
import com.citrus.util.ext.viewBinding
import com.citrus.util.onSafeClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import soup.neumorphism.ShapeType
import javax.inject.Inject

@AndroidEntryPoint
class CurrentFragment : BaseFragment(R.layout.fragment_current) {
    private val binding by viewBinding(FragmentCurrentBinding::bind)
    private val viewModel: CurrentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    var scope = CoroutineScope(Job() + Dispatchers.Main)

    var hasRes = false


    @Inject
    lateinit var currentAcAdapter: CurrentAcAdapter

    @Inject
    lateinit var memoAdapter: MemoAdapter

    @Inject
    lateinit var resAdapter: ResAdapter

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

            memoRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = memoAdapter
            }

            resRv.apply {
                layoutManager = GridLayoutManager(requireContext(), 2)
                adapter = resAdapter
            }

            llMemo.onSafeClick {
                if (llMemo.getShapeType() == ShapeType.FLAT) {
                    llMemo.setShapeType(ShapeType.PRESSED)
                    memoRv.visibility = View.VISIBLE
                    ivMemo.setImageResource(R.drawable.ic_baseline_unfold_less_24)
                } else {
                    llMemo.setShapeType(ShapeType.FLAT)

                    memoRv.visibility = if (hasRes) View.GONE else View.INVISIBLE
                    ivMemo.setImageResource(R.drawable.ic_baseline_unfold_more_24)
                }
            }

            llRes.onSafeClick {
                if (llRes.getShapeType() == ShapeType.FLAT) {
                    llRes.setShapeType(ShapeType.PRESSED)
                    resRv.isVisible = true
                    ivRes.setImageResource(R.drawable.ic_baseline_unfold_less_24)
                } else {
                    llRes.setShapeType(ShapeType.FLAT)
                    resRv.isVisible = false
                    ivRes.setImageResource(R.drawable.ic_baseline_unfold_more_24)
                }
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
                    if (it.exception == Constants.NO_DATA) {
                        binding.rvCurrent.isVisible = false
                        binding.noneInfo.isVisible = true
                        binding.noneInfo2.isVisible = true
                    } else {
                        showErrDialog(
                            title = "與系統連線發生問題",
                            msg = it.exception,
                            onConfirmListener = {
                                viewModel.getRecordLatest()
                            }
                        )
                    }
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
                val notes = info.notes.map { it.note }
                CoroutineScope(Dispatchers.Main).launch {
                    memoAdapter.updateDataset(notes as MutableList<String>)
                }
                binding.tvMemoSize.text = "${notes.size}"

                binding.llMemo.isVisible = true
                binding.llMemo.setShapeType(ShapeType.PRESSED)
                binding.memoRv.visibility = View.VISIBLE
            } else {
                binding.llMemo.isVisible = false
                binding.memoRv.visibility = View.GONE
            }


            if (info.resData.isNotEmpty()) {
                hasRes = true
                CoroutineScope(Dispatchers.Main).launch {
                    resAdapter.updateDataset(info.resData as MutableList<Reservation>)
                }
                binding.tvResSize.text = "${info.resData.size}"

                binding.llRes.isVisible = true
                binding.llRes.setShapeType(ShapeType.PRESSED)
                binding.resRv.isVisible = true
            } else {
                binding.llRes.isVisible = false
                binding.resRv.isVisible = false
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