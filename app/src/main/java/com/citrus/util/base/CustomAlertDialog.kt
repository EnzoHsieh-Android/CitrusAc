package com.citrus.util.base

import android.content.Context
import android.view.View
import com.citrus.citrusac.databinding.DialogAlertBinding
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class CustomAlertDialog(
    mContext: Context,
    private var title: String,
    private var msg: String,
    private val icon: Int,
    private val onConfirmListener: () -> Unit,
    private val onCancelListener: (() -> Unit)? = null
) : BindingAlertDialog<DialogAlertBinding>(mContext, DialogAlertBinding::inflate) {

    constructor(mContext: Context, title: String, msg: String, icon: Int) : this(
        mContext,
        title,
        msg,
        icon,
        {})

    override fun initView() {
        setCanceledOnTouchOutside(false)
        binding.apply {
            YoYo.with(Techniques.FlipInX).delay(100).duration(1000)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).playOn(ivIcon)
            ivIcon.setImageResource(icon)
            tvTitle.text = title

            if (msg.isNotEmpty()) {
                tvMessage.visibility = View.VISIBLE
                tvMessage.text = msg
            }

            btnOK.setOnClickListener {
                onConfirmListener()
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
                onCancelListener?.invoke()
            }

        }
    }

    fun setCancelBtnVisible() {
        binding.btnCancel.visibility = View.VISIBLE
    }

}