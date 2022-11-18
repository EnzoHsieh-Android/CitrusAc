package com.citrus.citrusac.present.history

import android.annotation.SuppressLint
import android.view.View
import com.citrus.citrusac.databinding.DailogDatePickerBinding
import com.citrus.util.base.BaseDialogFragment
import com.citrus.util.ext.toLocalTime
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import splitties.snackbar.snack
import splitties.views.onClick
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.*

@SuppressLint("SetTextI18n")
class CustomDatePickerDialog(
    private var selectedDateTimes: List<LocalDateTime>,
    private val datePickType: DatePickType = DatePickType.OnlyDate,
    private val maxSelectedSize: Int = 3,
    private val listener: (List<LocalDateTime>) -> Unit,
) : BaseDialogFragment<DailogDatePickerBinding>(DailogDatePickerBinding::inflate,false) {


    var selectedDates = selectedDateTimes.map { it.toLocalDate() }.toMutableList()


    override fun initView() {
        isCancelable = true

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
                DatePickType.DateAndTime -> Unit
            }





            btnOK.onClick {
                if (selectedDates.isEmpty()) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)
                    binding.root.snack("尚未選擇區間")
                    return@onClick
                }

                if (selectedDates.size > maxSelectedSize) {
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)

                    binding.root.snack(
                        String.format(
                            "僅能查詢三日內門禁資訊",
                            maxSelectedSize
                        )
                    )
                    return@onClick
                }


                val selectedDateTimes = selectedDates.map { it.atStartOfDay() }.toMutableList()
                if (selectedDateTimes.size == 1) {
                    selectedDateTimes.add(selectedDateTimes.first()) //加一個date來放結尾時間
                }
                selectedDateTimes[0] = selectedDateTimes.first()
                    .with("${(tvStartTime.text.toString())}:00".toLocalTime())
                selectedDateTimes[selectedDateTimes.size - 1] =
                    selectedDateTimes.last().with("${(tvEndTime.text.toString())}:59".toLocalTime())

                if (selectedDateTimes.first().isAfter(selectedDateTimes.last())) {
                    binding.root.snack("輸入錯誤")
                    YoYo.with(Techniques.Shake).duration(1000).playOn(calendarView)
                    return@onClick
                }



                listener(selectedDateTimes)
                dismiss()
            }

            btnClose.onClick {
                dismiss()
            }
        }
    }

    private fun DailogDatePickerBinding.initDateText() {
        tvStartTimeWeek.text =
            selectedDates.first().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        tvEndTimeWeek.text =
            selectedDates.last().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        tvStartDate.text = "${
            selectedDates.first().month.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        } ${selectedDates.first().dayOfMonth}"
        tvEndDate.text = "${
            selectedDates.last().month.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
        } ${selectedDates.last().dayOfMonth}"
    }



    override fun initAction() {

    }

    override fun clearMemory() {

    }


}


sealed class DatePickType {
    object OnlyDate : DatePickType()
    object DateAndTime : DatePickType()
}