package com.citrus.citrusac.present.current.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.citrus.citrusac.R
import com.citrus.citrusac.databinding.ItemCurrentBinding
import com.citrus.citrusac.present.main.PageType
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants.getLogServerTime
import com.citrus.util.base.BindingAdapter
import com.citrus.util.onSafeClick
import kotlinx.coroutines.delay
import soup.neumorphism.ShapeType
import splitties.views.onClick


class CurrentAcAdapter :
    BindingAdapter<ItemCurrentBinding, AccessLatest>(
        ItemCurrentBinding::inflate,
        PageType.Current
    ) {

    override fun convert(binding: ItemCurrentBinding, item: AccessLatest, position: Int) {
        binding.apply {
            userName.text = item.name
            userPid.text = item.custNo
            ivNote.isVisible = item.notes.isNotEmpty()
            llInfo.setShapeType(if (item.selected) ShapeType.PRESSED else ShapeType.FLAT)

            Glide.with(root)
                .load(item.photoPath)
                .placeholder(R.drawable.default_user)
                .into(avatar)


            enterDate.text = getLogServerTime(item.logTime)

            root.onSafeClick {
                data.forEach {
                    it.selected = false
                }

                item.selected = true

                onClickListener?.invoke(item)
            }


            avatar.strokeColor = ColorStateList(
                arrayOf(intArrayOf()),
                intArrayOf(
                    ContextCompat.getColor(
                        root.context,
                        if (item.selected) R.color.white else R.color.colorGoldDeep
                    )
                )
            )
        }
    }


    private var onClickListener: ((AccessLatest) -> Unit)? = null
    fun setOnClickListener(listener: (AccessLatest) -> Unit) {
        onClickListener = listener
    }

    override fun payloadConvert(
        payload: Any,
        binding: ItemCurrentBinding,
        item: AccessLatest,
        position: Int
    ) {

    }

}