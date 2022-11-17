package com.citrus.citrusac.present.history.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.ItemCurrentBinding
import com.citrus.citrusac.databinding.ItemHistoryBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.remote.vo.AccessHistory
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants.getLogServerDateTime
import com.citrus.util.Constants.getLogServerTime
import com.citrus.util.base.BindingAdapter
import com.citrus.util.onSafeClick
import soup.neumorphism.ShapeType
import splitties.views.onClick


class HistoryAcAdapter :
    BindingAdapter<ItemHistoryBinding, AccessHistory>(
        ItemHistoryBinding::inflate,
        PageType.History
    ) {

    override fun convert(binding: ItemHistoryBinding, item: AccessHistory, position: Int) {
        binding.apply {

            ivStatus.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    root.context,
                    if (item.status == "I") R.color.green else R.color.red
                )
            )

            tvCustNo.text = item.custNo
            tvName.text = item.name
            tvLogTime.text = getLogServerDateTime(item.logTime)


            if (item.selected) {
                llInfo.setShapeType(ShapeType.PRESSED)
            } else {
                llInfo.setShapeType(ShapeType.FLAT)
            }


            root.onSafeClick {
                if (!item.selected) {
                    var oldPos = -1
                    data.forEachIndexed { index, accessHistory ->
                        if (accessHistory.selected) {
                            accessHistory.selected = false
                            oldPos = index
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
    ) {

    }

}