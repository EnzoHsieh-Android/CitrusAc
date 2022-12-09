package com.citrus.citrusac.present.history

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.FragmentHistoryBinding
import com.citrus.citrusac.present.current.adapter.MemoAdapter
import com.citrus.citrusac.present.current.adapter.ResAdapter
import com.citrus.citrusac.present.history.adapter.HistoryAcAdapter
import com.citrus.citrusac.present.main.PageType
import com.citrus.citrusac.present.main.SharedViewModel
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessHistory
import com.citrus.remote.vo.AccessLatest
import com.citrus.remote.vo.NoteData
import com.citrus.remote.vo.Reservation
import com.citrus.util.Constants
import com.citrus.util.base.BaseFragment
import com.citrus.util.base.CustomAlertDialog
import com.citrus.util.ext.*
import com.citrus.util.onSafeClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import soup.neumorphism.ShapeType
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

    @Inject
    lateinit var memoAdapter: MemoAdapter

    @Inject
    lateinit var resAdapter: ResAdapter

    var customDialog: CustomAlertDialog? = null

    var hasRes = false

    companion object {
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedViewModel.setViewPagerSwitch(PageType.History)
        viewModel.startQuery()
        binding.rvHistory.scrollToPosition(0)
    }

    override fun onPause() {
        super.onPause()
        binding.tvQuery.text.clear()
        binding.tvStartTime.text = LocalDateTime.now().toDateStr() + "\n" + " 00:00:00"
        binding.tvEndTime.text = LocalDateTime.now().toDateStr() + "\n" + " 23:59:59"
    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        binding.apply {
            tvStartTime.text = LocalDateTime.now().toDateStr() + "\n" + " 00:00:00"
            tvEndTime.text = LocalDateTime.now().toDateStr() + "\n" + " 23:59:59"

            rvHistory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = historyAcAdapter
            }

            tvStartTime.onClick {
                CustomDatePickerDialog(
                    listOf(LocalDateTime.now()),
                    datePickType = DatePickType.DateAndTime,
                    maxSelectedSize = 3,
                ) { selectedDates ->
                    tvStartTime.text = selectedDates.first().toDateStr3()
                        .split(" ")[0] + "\n" + selectedDates.first().toDateStr3().split(" ")[1]

                    tvEndTime.text = selectedDates.last().toDateStr3()
                        .split(" ")[0] + "\n" + selectedDates.last().toDateStr3().split(" ")[1]


                    val dates =
                        listOf(
                            selectedDates.first().toDateStr4(),
                            selectedDates.last().toDateStr4()
                        )

                    viewModel.accept(UiAction.SearchDate(dates = dates))
                }.show(childFragmentManager, "CustomDatePickerDialog")
            }

            tvEndTime.onSafeClick {
                tvStartTime.performClick()
            }

            tvQuery.doOnTextChanged { text, _, _, _ ->
                viewModel.accept(UiAction.SearchStr(queryStr = text.toString()))
            }

            llDateClear.onClick {
                tvStartTime.text = "-"
                tvEndTime.text = "-"

                val dates = listOf("", "")

                viewModel.accept(UiAction.SearchDate(dates = dates))
            }

            llPidClear.onClick {
                tvQuery.setText("")
                viewModel.accept(UiAction.SearchStr(queryStr = ""))
            }

            memoRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = memoAdapter
            }

            resRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = resAdapter
            }

            llMemo.onSafeClick {
                if (llMemo.getShapeType() == ShapeType.FLAT) {
                    llMemo.setShapeType(ShapeType.PRESSED)
                    memoRv.isVisible = true
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

            refresh.setOnRefreshListener {
                viewModel.startQuery()
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
            binding.refresh.isRefreshing = false
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
                    } else {
                        viewModel.setDataEmpty(true)

                        customDialog?.dismiss()
                        customDialog = showDialog(
                            "與系統連線發生問題",
                            it.exception,
                            onConfirmListener = null,
                            onCancelListener = null
                        )
                        customDialog?.show()
                        customDialog?.setCancelBtnVisible()
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

        lifecycleFlow(sharedViewModel.memberRes) { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    if (result.data.isNotEmpty()) {
                        hasRes = true
                        CoroutineScope(Dispatchers.Main).launch {
                            resAdapter.updateDataset(result.data as MutableList<Reservation>)
                        }
                        binding.tvResSize.text = "${result.data.size}"

                        binding.llRes.isVisible = true
                        binding.llRes.setShapeType(ShapeType.PRESSED)
                        binding.resRv.isVisible = true
                    }
                }
                is Resource.Error -> {
                    binding.llRes.isVisible = false
                    binding.resRv.isVisible = false
                }
            }
        }

        lifecycleFlow(sharedViewModel.memberNotes) { result ->
            when (result) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    if (result.data.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            memoAdapter.updateDataset(result.data as MutableList<NoteData>)
                        }
                        binding.tvMemoSize.text = "${result.data.size}"

                        binding.llMemo.isVisible = true
                        binding.llMemo.setShapeType(ShapeType.PRESSED)
                        binding.memoRv.visibility = View.VISIBLE
                    }
                }
                is Resource.Error -> {
                    binding.llMemo.isVisible = false
                    binding.memoRv.visibility = View.GONE
                }
            }
        }
    }

    override fun initAction() {
        historyAcAdapter.setOnClickListener { custNo ->
            viewModel.getAcDetail(custNo)
        }

        memoAdapter.setOnMemoDoneListener { noteData ->
            sharedViewModel.setMemoValid(noteData)
            Log.e("CurrentFragment", "onMemoDone: $noteData")
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


            llDetailInfo.isVisible = true
            binding.llMemo.isVisible = false
            binding.memoRv.visibility = View.GONE
            binding.llRes.isVisible = false
            binding.resRv.isVisible = false

            sharedViewModel.fetchMemoAndRes(info.custNo)
        }
    }

}