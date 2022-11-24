package com.citrus.util.base


import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.citrus.util.Constants
import com.citrus.util.i18n.LocaleHelper


abstract class BaseActivity<VB : ViewBinding>(private val inflate: InflateActivity<VB>) : AppCompatActivity() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    protected abstract fun initView()
    protected abstract fun initObserve()

    override fun onResume() {
        super.onResume()
        setFullScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(layoutInflater)
        setContentView(binding.root)

        val size = Point()
        val display = windowManager.defaultDisplay
        display.getSize(size)
        /**儲存螢幕size*/
        Constants.screenW = size.x
        Constants.screenH = size.y

        initView()
        initObserve()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val isTouchOnView: Boolean
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                isTouchOnView = outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED)

                if (!isTouchOnView) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }

                v.isCursorVisible = isTouchOnView
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    fun setFullScreen() {
        val decorView = setSystemUiVisibilityMode()
        decorView.setOnSystemUiVisibilityChangeListener {
            setSystemUiVisibilityMode() // Needed to avoid exiting immersive_sticky when keyboard is displayed
        }
    }

    private fun setSystemUiVisibilityMode(): View {
        val decorView = window.decorView
        val options = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        decorView.systemUiVisibility = options
        return decorView
    }

}