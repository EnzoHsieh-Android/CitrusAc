package com.citrus.citrusac.present.main


import android.util.TypedValue
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.ActivityMainBinding
import com.citrus.citrusac.present.current.ScanQrDialog
import com.citrus.citrusac.present.setting.SettingFragment
import com.citrus.di.prefs
import com.citrus.util.base.BaseActivity
import com.citrus.util.onSafeClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val sharedViewModel: SharedViewModel by viewModels()


    override fun initView() {
        pageTypeChange(PageType.Current)

        binding.apply {
            if (prefs.serverIp.isBlank() || prefs.deviceId.isBlank()) {
                SettingFragment().show(supportFragmentManager, "SettingFragment")
            }


            setting.onSafeClick {
                SettingFragment().show(supportFragmentManager, "SettingFragment")
            }

            llCheck.onSafeClick {
                if (tvCustNo.text.isNotBlank()) {
                    sharedViewModel.setAcData(custNo = tvCustNo.text.toString())
                }
            }

            llQr.onSafeClick {
                ScanQrDialog { scanResult ->
                    MainScope().launch {
                        tvCustNo.setText(scanResult)
                        llCheck.performClick()
                    }
                }.show(supportFragmentManager, "ScanFragment")
            }

            tvCustNo.doOnTextChanged { text, _, _, _ ->
                if (text.isNullOrBlank()) {
                    llCheck.isVisible = false
                    llQr.isVisible = true
                } else {
                    llCheck.isVisible = true
                    llQr.isVisible = false
                }
            }

            tvCustNo.setOnEditorActionListener { _, id, _ ->
                if (id == EditorInfo.IME_ACTION_DONE) {
                    if (tvCustNo.text.isNotBlank()) {
                        llCheck.performClick()
                        tvCustNo.clearFocus()
                    }
                }
                false
            }

            tvCurrent.onSafeClick {
                pageTypeChange(PageType.Current)
                sharedViewModel.setViewPagerSwitch(PageType.Current)
            }

            tvHistory.onSafeClick {
                pageTypeChange(PageType.History)
                sharedViewModel.setViewPagerSwitch(PageType.History)
            }

        }
    }

    override fun initObserve() {
        lifecycleScope.launchWhenStarted {
            sharedViewModel.acTitleChange.collectLatest {
                //binding.tvTitle.text = it
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.setAcDataSuccess.collectLatest {
                binding.tvCustNo.text.clear()
            }
        }

        lifecycleScope.launchWhenStarted {
            sharedViewModel.setPageType.collectLatest {
                pageTypeChange(it)
            }
        }

    }

    private fun pageTypeChange(pageType: PageType) {
        when (pageType) {
            is PageType.Current -> {
                binding.llCurrent.visibility = View.VISIBLE
                binding.llHistory.visibility = View.INVISIBLE
                binding.tvCurrent.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.dp_18)
                )
                binding.tvCurrent.setTextColor(resources.getColor(R.color.white))

                binding.tvHistory.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.dp_14)
                )
                binding.tvHistory.setTextColor(resources.getColor(R.color.colorGoldDeep))
            }
            is PageType.History -> {
                binding.llCurrent.visibility = View.INVISIBLE
                binding.llHistory.visibility = View.VISIBLE

                binding.tvCurrent.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.dp_14)
                )
                binding.tvCurrent.setTextColor(resources.getColor(R.color.colorGoldDeep))

                binding.tvHistory.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    resources.getDimension(R.dimen.dp_18)
                )
                binding.tvHistory.setTextColor(resources.getColor(R.color.white))
            }
        }
    }
}