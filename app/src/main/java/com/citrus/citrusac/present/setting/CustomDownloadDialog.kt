package com.citrus.citrusac.present.setting

import android.content.Context
import com.citrus.citrusac.databinding.DialogDownloadBinding
import com.citrus.util.base.BindingAlertDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo


class CustomDownloadDialog(
    mContext: Context,
    private val onConfirmListener: (String) -> Unit
) : BindingAlertDialog<DialogDownloadBinding>(mContext, DialogDownloadBinding::inflate) {

    override fun initView() {
        binding.apply {
            btnOK.setOnClickListener {
                val version = etCode.text.toString().replace(" ", "")
                if (!version.isNullOrEmpty()) {
                    onConfirmListener(version)
                    dismiss()
                } else {
                    YoYo.with(Techniques.Shake).delay(100).duration(1000)
                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).playOn(etCode)
                    YoYo.with(Techniques.Shake).delay(100).duration(1000)
                        .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT).playOn(codeInputLayout)
                }
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }
}