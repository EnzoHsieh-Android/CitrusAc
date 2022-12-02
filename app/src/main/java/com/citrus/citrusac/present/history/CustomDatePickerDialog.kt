package com.citrus.citrusac.present.history

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.citrus.citrusac.databinding.DailogDatePickerBinding
import com.citrus.util.base.BaseDialogFragment
import com.citrus.util.ext.toHourMinStr
import com.citrus.util.ext.toLocalTime
import com.citrus.util.onSafeClick
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import splitties.snackbar.snack
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n")
class CustomDatePickerDialog(
    private var selectedDateTimes: List<LocalDateTime>,
    private val datePickType: DatePickType,
    private val maxSelectedSize: Int? = null,
    private val listener: (List<LocalDateTime>) -> Unit,
) : BaseDialogFragment<DailogDatePickerBinding>(DailogDatePickerBinding::inflate, false) {

    var selectedDates = selectedDateTimes.map { it.toLocalDate() }.toMutableList()

    override fun initView() {
        isCancelable = false
        //isTablet
        setWindowWidthPercent(0.65, 0.85)

        binding.apply {

            calendarView.setDates(selectedDates)
            calendarView.onDateSelected = {
                initDateText()
            }

            initDateText()

            when (datePickType) {
                DatePickType.OnlyDate -> {
                    groupTime.visibility = View.GONE
                    tvStartTime.text = "00:00"
                    tvEndTime.text = "23:59"
                }
                DatePickType.DateAndTime -> {
                    try {
                        tvStartTime.text = selectedDateTimes.first().toHourMinStr()
                        tvEndTime.text = selectedDateTimes.last().toHourMinStr()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showError(e)
                        return@apply
                    }
                }
            }



            tvStartTime.onSafeClick {
                showTimePickerDialog(it as TextView)
            }

            tvEndTime.onSafeClick {
                showTimePickerDialog(it as TextView)
            }


            btnOK.onSafeClick {
                if (selectedDates.isEmpty()) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)
                    binding.root.snack("尚未完成選曲")
                    return@onSafeClick
                }

                if (maxSelectedSize != null && selectedDates.size > maxSelectedSize) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)
                    binding.root.snack("日期所選數量已超過可查詢總天數")
                    return@onSafeClick
                }


                val selectedDateTimes = selectedDates.map { it.atStartOfDay() }.toMutableList()
                if (selectedDateTimes.size == 1) {
                    selectedDateTimes.add(selectedDateTimes.first()) //加一個date來放結尾時間
                }
                selectedDateTimes[0] = selectedDateTimes.first().with("${(tvStartTime.text.toString())}:00".toLocalTime())
                selectedDateTimes[selectedDateTimes.size - 1] = selectedDateTimes.last().with("${(tvEndTime.text.toString())}:59".toLocalTime())

                if (selectedDateTimes.first().isAfter(selectedDateTimes.last())) {
                    binding.root.snack("參數錯誤")
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)
                    return@onSafeClick
                }


                listener(selectedDateTimes)
                dismiss()
            }

            btnClose.onSafeClick {
                dismiss()
            }
        }
    }

    private fun DailogDatePickerBinding.initDateText() {
        tvStartTimeWeek.text = selectedDates.first().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        tvEndTimeWeek.text = selectedDates.last().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        tvStartDate.text = "${selectedDates.first().month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${selectedDates.first().dayOfMonth}"
        tvEndDate.text = "${selectedDates.last().month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${selectedDates.last().dayOfMonth}"
    }

    private fun showError(e: Exception) {
        //showAlertDialog("ERROR", e.toString())
        dismiss()
    }

    override fun initAction() {
    }

    private fun showTimePickerDialog(v: TextView) {
        TimePickerFragment(v.text.toString()) {
            v.text = it
        }.show(childFragmentManager, "timePicker")
    }

    override fun clearMemory() {

    }
}


sealed class DatePickType {
    object OnlyDate : DatePickType()
    object DateAndTime : DatePickType()
}