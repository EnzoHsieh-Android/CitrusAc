package com.citrus.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.CalendarDayLayoutBinding
import com.citrus.citrusac.databinding.CustomCalendarViewBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import splitties.views.onClick
import splitties.views.textColorResource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*



@SuppressLint("SetTextI18n")
class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var binding: CustomCalendarViewBinding = CustomCalendarViewBinding.inflate(LayoutInflater.from(context), this, true)
    lateinit var selectedDates: MutableList<LocalDate>
    val enableDateSize = 70L
    var onDateSelected: () -> Unit = {}

    init {
        binding.apply {
            val currentMonth = YearMonth.now()
            val firstMonth = currentMonth.minusMonths(3)
            val lastMonth = currentMonth.plusMonths(1)
            val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
            calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
            calendarView.scrollToMonth(currentMonth)

            class DayViewContainer(view: View) : ViewContainer(view) {
                val binding = CalendarDayLayoutBinding.bind(view)
            }

            calendarView.dayBinder = object : DayBinder<DayViewContainer> {
                // Called only when a new container is needed.
                override fun create(view: View) = DayViewContainer(view)

                // Called every time we need to reuse a container.
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    val tvDay = container.binding.calendarDayText
                    val leftRangeBg = container.binding.leftRangeBg
                    val rightRangeBg = container.binding.rightRangeBg
                    tvDay.text = day.date.dayOfMonth.toString()
                    tvDay.typeface = Typeface.DEFAULT
                    tvDay.textColorResource = R.color.colorPrimaryText
                    leftRangeBg.isVisible = false
                    rightRangeBg.isVisible = false
                    tvDay.background = null

                    if (selectedDates.contains(day.date) && day.owner == DayOwner.THIS_MONTH) {
                        tvDay.setTypeface(tvDay.typeface, Typeface.BOLD)
                        if (day.date in setOf(selectedDates.first(), selectedDates.last())) {
                            tvDay.setBackgroundResource(R.drawable.button_mid_yellow_15)
                        }

                        if (selectedDates.size > 1 && selectedDates.first() != selectedDates.last()) {
                            when (day.date) {
                                selectedDates.first() -> {
                                    rightRangeBg.isVisible = true
                                }
                                selectedDates.last() -> {
                                    leftRangeBg.isVisible = true
                                }
                                else -> {
                                    rightRangeBg.isVisible = true
                                    leftRangeBg.isVisible = true
                                }
                            }
                        }
                    } else {
                        if (day.owner != DayOwner.THIS_MONTH || day.date < LocalDate.now().minusDays(enableDateSize)) {
                            tvDay.textColorResource = R.color.colorLightGray2
                        }
                    }


                    if (day.owner == DayOwner.THIS_MONTH && day.date >= LocalDate.now().minusDays(enableDateSize)) {
                        container.binding.root.isEnabled = true
                        container.binding.root.onClick {
                            val date = day.date
                            if (selectedDates.isNotEmpty()) {
                                if (date <= selectedDates.first() || selectedDates.size > 1) { //一天
                                    selectedDates.clear()
                                    selectedDates.add(date)
                                } else { //範圍
                                    val range = DateTimeUtil.getDatesBetweenTwoDates(selectedDates.first(), date).toMutableList()
                                    selectedDates.clear()
                                    selectedDates.addAll(range)
                                    selectedDates.add(date)
                                }
                            } else {
                                selectedDates.add(date)
                            }
                            binding.calendarView.notifyCalendarChanged()
//                            Timber.tag("TEST").d("selectedDates: $selectedDates")
                            onDateSelected()
                        }
                    } else {
                        container.binding.root.isEnabled = false
                        container.binding.root.setOnClickListener(null)
                    }
                }
            }

            class MonthViewContainer(view: View) : ViewContainer(view) {
                val textView = view.findViewById<TextView>(R.id.headerTextView)
            }
            calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                    val monthName = month.yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    container.textView.text = "$monthName ${month.year}"
                }
            }

            legendLayout.legendText1.text = DayOfWeek.SUNDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText2.text = DayOfWeek.MONDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText3.text = DayOfWeek.TUESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText4.text = DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText5.text = DayOfWeek.THURSDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText6.text = DayOfWeek.FRIDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()
            legendLayout.legendText7.text = DayOfWeek.SATURDAY.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase()

        }
    }

    fun setDates(dates: MutableList<LocalDate>) {
        selectedDates = dates
        binding.calendarView.notifyCalendarChanged()
    }

}