package com.citrus.citrusac.present.setting

import com.citrus.citrusac.BuildConfig
import com.citrus.citrusac.databinding.FragmentSettingBinding
import com.citrus.di.prefs
import com.citrus.util.base.BaseDialogFragment
import com.citrus.util.onSafeClick
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class SettingFragment :
    BaseDialogFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate, true) {

    override fun initView() {
        isCancelable = false

        binding.apply {
            etServerIp.setText(prefs.serverIp)
            etDeviceId.setText(prefs.deviceId)

            tvVersion.text =  "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})${if (BuildConfig.DEBUG) " - DEBUG" else ""}"

            llCheck.onSafeClick {
                if (etServerIp.text.isBlank()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(etServerIp)
                    return@onSafeClick
                } else {
                    prefs.serverIp = etServerIp.text.trim().toString().replace(" ", "")
                }

                if (etDeviceId.text.isBlank()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(etDeviceId)
                    return@onSafeClick
                } else {
                    prefs.deviceId = etDeviceId.text.trim().toString().replace(" ", "")
                }

                dismiss()
            }
        }
    }

    override fun initAction() {

    }

    override fun clearMemory() {

    }

}