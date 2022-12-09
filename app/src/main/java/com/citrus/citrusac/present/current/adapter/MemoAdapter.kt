package com.citrus.citrusac.present.current.adapter

import android.graphics.Paint
import android.widget.TextView
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.ItemMemoBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.remote.vo.NoteData
import com.citrus.util.base.BindingAdapter


class MemoAdapter :
    BindingAdapter<ItemMemoBinding, NoteData>(
        ItemMemoBinding::inflate,
        PageType.History
    ) {

    override fun convert(binding: ItemMemoBinding, item: NoteData, position: Int) {
        binding.apply {
            tvSeq.text = (item.seq).toString()
            tvMemo.text = item.note


            viewLine.background = context.resources.getDrawable(
                if (position % 2 == 0) R.color.colorGoldDeep else R.color.colorGold,
                null
            )

            viewLine2.background = context.resources.getDrawable(
                if (position % 2 == 0) R.color.colorGoldDeep else R.color.colorGold,
                null
            )

            cbDown.isChecked = item.selected

            cbDown.setOnCheckedChangeListener { _, b ->
                if(b) {
                    onMemoDoneListener?.invoke(item)
                    item.selected = true
                    tvMemo.paintFlags =  Paint.STRIKE_THRU_TEXT_FLAG
                    tvMemo.setTextColor(context.resources.getColor(R.color.colorDeepGray, null))
                    root.isEnabled = false
                    cbDown.isClickable = false
                }else {
                    return@setOnCheckedChangeListener
                }
            }
        }
    }


    private var onMemoDoneListener: ((NoteData) -> Unit)? = null
    fun setOnMemoDoneListener(listener: (NoteData) -> Unit) {
        onMemoDoneListener = listener
    }

    override fun payloadConvert(
        payload: Any,
        binding: ItemMemoBinding,
        item: NoteData,
        position: Int
    ) = Unit

}