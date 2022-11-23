package com.citrus.citrusac.present.current.adapter

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
            tvMemo.text = item
        }
    }


    override fun payloadConvert(
        payload: Any,
        binding: ItemMemoBinding,
        item: String,
        position: Int
    ) = Unit

}