package com.citrus.citrusac.present.history.adapter

import android.annotation.SuppressLint
import com.citrus.citrusac.databinding.ItemHistoryBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.remote.vo.AccessHistory
import com.citrus.util.Constants.getLogServerDateTime
import com.citrus.util.base.BindingAdapter
import com.citrus.util.onSafeClick
import soup.neumorphism.ShapeType


class HistoryAcAdapter :
    BindingAdapter<ItemHistoryBinding, AccessHistory>(
        ItemHistoryBinding::inflate,
        PageType.History
    ) {

    @SuppressLint("NotifyDataSetChanged")
    override fun convert(binding: ItemHistoryBinding, item: AccessHistory, position: Int) {
        binding.apply {


            tvCustNo.text = item.custNo
            tvPid.text = item.pid
            tvName.text = item.name

            val time = getLogServerDateTime(item.logTime).split(" ")
            tvLogTime.text = time[0] + "\n" + time[1]


            if (item.selected) {
                llInfo.setShapeType(ShapeType.PRESSED)
            } else {
                llInfo.setShapeType(ShapeType.FLAT)
            }


            root.onSafeClick {
                if (!item.selected) {
                    data.forEachIndexed { _, accessHistory ->
                        if (accessHistory.selected) {
                            accessHistory.selected = false
                        }
                    }

                    item.selected = true
                    onClickListener?.invoke(item.custNo)

                    notifyDataSetChanged()
                }
            }

        }
    }


    private var onClickListener: ((String) -> Unit)? = null
    fun setOnClickListener(listener: (String) -> Unit) {
        onClickListener = listener
    }

    override fun payloadConvert(
        payload: Any,
        binding: ItemHistoryBinding,
        item: AccessHistory,
        position: Int
    ) = Unit

}