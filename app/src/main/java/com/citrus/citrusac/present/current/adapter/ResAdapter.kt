package com.citrus.citrusac.present.current.adapter

import com.citrus.citrusac.databinding.ItemResBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.remote.vo.Reservation
import com.citrus.util.Constants
import com.citrus.util.base.BindingAdapter


class ResAdapter :
    BindingAdapter<ItemResBinding, Reservation>(
        ItemResBinding::inflate,
        PageType.History
    ) {

    override fun convert(binding: ItemResBinding, item: Reservation, position: Int) {
        binding.apply {
            tvTime.text = Constants.getResServerTime(item.resTime)
            tvCusNum.text = item.custNum.toString() + "人"
            tvMemo.text = "備註：" + item.memo.ifBlank { "無" }
        }
    }


    override fun payloadConvert(
        payload: Any,
        binding: ItemResBinding,
        item: Reservation,
        position: Int
    ) = Unit

}