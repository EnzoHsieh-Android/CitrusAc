package com.citrus.citrusac.present.history

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.FragmentHistoryBinding
import com.citrus.citrusac.present.history.adapter.HistoryAcAdapter
import com.citrus.citrusac.present.main.PageType
import com.citrus.citrusac.present.main.SharedViewModel
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessHistory
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants
import com.citrus.util.base.BaseFragment
import com.citrus.util.ext.*
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.onClick
import java.time.LocalDateTime
import javax.inject.Inject


@AndroidEntryPoint
class HistoryFragment : BaseFragment(R.layout.fragment_history) {
    private val binding by viewBinding(FragmentHistoryBinding::bind)
    private val viewModel: HistoryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    @Inject
    lateinit var historyAcAdapter: HistoryAcAdapter

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setViewPagerSwitch(PageType.History)
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            tvDate.text = LocalDateTime.now().toDateStr() + " ~ " + LocalDateTime.now().toDateStr()

            rvHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAcAdapter
            }

            tvDate.onClick {
                CustomDatePickerDialog(listOf(LocalDateTime.now())) { selectedDates ->
                    tvDate.text =
                        selectedDates.first().toDateStr() + " ~ " + selectedDates.last().toDateStr()
                    val dates =
                        listOf(
                            selectedDates.first().toDateStr2(),
                            selectedDates.last().toDateStr2()
                        )
                    viewModel.accept(UiAction.SearchDate(dates = dates))
                }.show(childFragmentManager, "CustomDatePickerDialog")

            }

            tvQuery.doOnTextChanged { text, _, _, _ ->
                viewModel.accept(UiAction.SearchStr(queryStr = text.toString()))
            }

            llDateClear.onClick {
                tvDate.text = ""
                tvDate.hint = "不限時段"

                val dates = listOf("", "")

                viewModel.accept(UiAction.SearchDate(dates = dates))
            }

            llPidClear.onClick {
                tvQuery.setText("")
                viewModel.accept(UiAction.SearchStr(queryStr = ""))
            }

        }

    }

    override fun initObserve() {
        lifecycleFlow(viewModel.acHistoryEmpty) {
            binding.apply {
                if (it) {
                    binding.noneInfo.isVisible = true
                    binding.noneInfo2.isVisible = true
                    binding.rvHistory.isVisible = false
                    binding.scroll.isVisible = false
                } else {
                    binding.noneInfo.isVisible = false
                    binding.noneInfo2.isVisible = false
                    binding.rvHistory.isVisible = true
                    binding.scroll.isVisible = true
                }
            }
        }


        lifecycleFlow(viewModel.acHistory) {
            when (it) {
                is Resource.Success -> {
                    it.data.firstOrNull()?.let { ac ->
                        ac.selected = true
                        viewModel.getAcDetail(ac.custNo)
                    }

                    historyAcAdapter.updateDataset(it.data as MutableList<AccessHistory>)
                }
                is Resource.Loading -> {
                    binding.loadingBar.isVisible = it.isLoading
                }
                is Resource.Error -> {
                    if (it.exception == Constants.NO_DATA) {
                        viewModel.setDataEmpty(true)
                    }else {
                        viewModel.setDataEmpty(true)
                        showErrDialog(
                            title = "與系統連線發生問題",
                            msg = it.exception,
                            onConfirmListener = {

                            }
                        )
                    }
                }
            }
        }

        lifecycleFlow(viewModel.acDetail) {
            when (it) {
                is Resource.Success -> {
                    updateInfoDetail(it.data)
                    viewModel.setDataEmpty(false)
                }
                is Resource.Loading -> Unit
                is Resource.Error -> Unit
            }
        }
    }

    override fun initAction() {
        historyAcAdapter.setOnClickListener { custNo ->
            viewModel.getAcDetail(custNo)
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updateInfoDetail(info: AccessLatest) {
        binding.apply {
            Glide.with(root)
                .load(info.photoPath)
                .placeholder(R.drawable.default_user)
                .into(avatar)

            tvName.text = info.name
            tvCustNo.text = info.custNo
            tvBirthday.text = Constants.getFromServerTime(info.birth)

            if (info.notes.isNotEmpty()) {
                Constants.setChips(
                    requireContext(),
                    info.notes.map { it.note },
                    binding.memoChip,
                    binding.tvBirthday.textSize
                )
                binding.hashTag.isVisible = true
                binding.llMemoChip.isVisible = true
            } else {
                binding.hashTag.isVisible = false
                binding.llMemoChip.isVisible = false
            }

            if (info.resData.isNotEmpty()) {
                llResInfo.visibility = View.VISIBLE
                resTime.text = Constants.getResServerTime(info.resData[0].resTime)
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