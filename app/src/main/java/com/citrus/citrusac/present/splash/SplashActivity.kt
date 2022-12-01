package com.citrus.citrusac.present.splash


import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import com.citrus.citrusac.databinding.ActivitySplashBinding
import com.citrus.citrusac.present.main.MainActivity
import com.citrus.util.base.BaseActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    override fun initView() {
        YoYo.with(Techniques.FadeIn).duration(3000)
            .onStart {
                binding.ivSplash.visibility = View.VISIBLE
            }.onEnd {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.playOn(binding.ivSplash)
    }

    override fun initObserve() = Unit

}