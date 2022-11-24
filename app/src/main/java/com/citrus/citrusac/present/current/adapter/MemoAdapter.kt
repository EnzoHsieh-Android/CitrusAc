package com.citrus.citrusac.present.current.adapter

import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.ItemMemoBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.util.base.BindingAdapter


class MemoAdapter :
    BindingAdapter<ItemMemoBinding, String>(
        ItemMemoBinding::inflate,
        PageType.History
    ) {

    override fun convert(binding: ItemMemoBinding, item: String, position: Int) {
        binding.apply {
            tvSeq.text = (position + 1).toString()
            tvMemo.text = item

            viewLine.background = context.resources.getDrawable(
                if (position % 2 == 0 ) R.color.colorGoldDeep else R.color.colorGold,
                null
            )

            viewLine2.background = context.resources.getDrawable(
                if (position % 2 == 0 ) R.color.colorGoldDeep else R.color.colorGold,
                null
            )
        }
    }


    override fun payloadConvert(
        payload: Any,
        binding: ItemMemoBinding,
        item: String,
        position: Int
    ) = Unit

}