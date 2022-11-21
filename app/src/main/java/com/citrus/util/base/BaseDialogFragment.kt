package com.citrus.util.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.citrus.citrusac.R
import com.citrus.util.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout



abstract class BaseDialogFragment<VB : ViewBinding>(private val inflate: Inflate<VB>, private val isSetting: Boolean) : DialogFragment() {
    private var _binding: VB? = null
    val binding get() = _binding!!

    private var isActive = false
    var isFullScreen = true

    private var mIsSetting = isSetting


    override fun onDestroy() {
        super.onDestroy()
        clearMemory()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isActive = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        isActive = false
        super.onDismiss(dialog)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.setBackgroundResource(if(mIsSetting)R.drawable.custom_setting_bg else R.drawable.custom_dialog_bg)
        setWindowWidthPercent()
        initView()
        initAction()
//        if (isFullScreen) {
//            setFullScreen() // if add this, click editText, window will not pop to top
//        }
    }

    abstract fun initView()
    abstract fun initAction()
    abstract fun clearMemory()

    //if add this, click editText, window will not pop to top
    open fun setFullScreen() {
        val decorView = dialog?.window?.decorView
        decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

//    fun showAlertDialog(title: String, message: String) {
//        val customDialog = CustomAlertDialog(
//            requireContext(), title, message,
//            R.drawable.ic_warning
//        )
//        customDialog.show()
//    }
//
//    fun showSuccessDialog(title: String, message: String) {
//        val customDialog = CustomAlertDialog(
//            requireContext(), title, message,
//            R.drawable.ic_check
//        )
//        customDialog.show()
//    }


//    fun showLoadingDialog() {
//        if (isActive) {
//            if (loadingDialog.isAdded) {
//                childFragmentManager.beginTransaction().remove(loadingDialog).commit()
//            }
//            loadingDialog.show(childFragmentManager, "LoadingDialog")
//        }
//    }
//
//    fun hideLoadingDialog() {
//        if (isActive) {
//            if (loadingDialog.isAdded) {
//                loadingDialog.dismiss()
//            }
//        }
//    }

    fun setWindowWidthPercent(wPct: Double = 0.65, hPct: Double = 0.6) {
        dialog?.window?.let {
            val width = if (wPct == 0.0) WindowManager.LayoutParams.WRAP_CONTENT else Constants.screenW * wPct
            val height = if (hPct == 0.0) WindowManager.LayoutParams.WRAP_CONTENT else  Constants.screenH * hPct

            it.setLayout((width).toInt(), (height).toInt())
            it.setGravity(Gravity.CENTER)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                if (ev.action == MotionEvent.ACTION_DOWN) {
                    val v = currentFocus
                    if (isShouldHideInput(v, ev)) {
                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
                    }
                    return super.dispatchTouchEvent(ev)
                }
                // Essential, otherwise all components will not have TouchEvent
                return if (window!!.superDispatchTouchEvent(ev)) {
                    true
                } else onTouchEvent(ev)
            }
        }
    }

    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null && (v is EditText || v is TextInputEditText || v is TextInputLayout)) {
            val leftTop = intArrayOf(0, 0)
            //Get the current location of the input box
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }
}


//dispatchTouchEvent issue
//abstract class BaseDialogFragmentOld<VB : ViewBinding>(private val inflate: Inflate<VB>) : SwipeDialogFragment() {
//    private var _binding: VB? = null
//    val binding get() = _binding!!
//
//    /**
//    [Hilt]java.lang.reflect.InvocationTargetException #2254 refer:https://github.com/google/dagger/issues/2254
//    Solution: renamed var activity to var mActivity
//     **/
//    var mActivity: AppCompatActivity? = null
//    private var isActive = false
//    private val loadingDialog = LoadingDialog.newInstance()
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        mActivity = context as AppCompatActivity
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        mActivity = null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        clearMemory()
//        _binding = null
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        isActive = true
////        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = inflate.invoke(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onDismiss(dialog: DialogInterface) {
//        isActive = false
//        super.onDismiss(dialog)
//    }
//
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        view.setBackgroundResource(R.drawable.custom_dialog_bg)
//        setFullScreen() // if add this, click editText, window will not pop to top
//        setWindowWidthPercent()
//        initView()
//        initAction()
//    }
//
//    abstract fun initView()
//    abstract fun initAction()
//    abstract fun clearMemory()
//
//    fun showAlertDialog(title: String, message: String) {
//        mActivity?.let {
//            val customDialog = CustomAlertDialog(
//                it, title, message,
//                R.drawable.ic_warning
//            )
//            customDialog.show()
//        }
//    }
//
//    fun showSuccessDialog(title: String, message: String) {
//        mActivity?.let {
//            val customDialog = CustomAlertDialog(
//                it, title, message,
//                R.drawable.ic_check
//            )
//            customDialog.show()
//        }
//    }
//
//
//    fun showLoadingDialog() {
//        if (isActive) {
//            if (loadingDialog.isAdded) {
//                childFragmentManager.beginTransaction().remove(loadingDialog).commit()
//            }
//            loadingDialog.show(childFragmentManager, "LoadingDialog")
//        }
//    }
//
//    fun hideLoadingDialog() {
//        if (isActive) {
//            if (loadingDialog.isAdded) {
//                loadingDialog.dismiss()
//            }
//        }
//    }
//
//    //if add this, click editText, window will not pop to top
//    open fun setFullScreen() {
//        val decorView = dialog?.window?.decorView
//        decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                View.SYSTEM_UI_FLAG_FULLSCREEN or
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//    }
//
//    private fun setWindowWidthPercent() {
//        dialog?.window?.let {
//            val size = Point()
//            val display = it.windowManager.defaultDisplay
//            display.getSize(size)
//
//            val width = size.x
//            val height = size.y
//
//            it.setLayout((width * 0.95).toInt(), (height * 0.95).toInt())
//            it.setGravity(Gravity.CENTER)
//        }
//    }
//
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        return object : Dialog(requireContext(), theme) {
//            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
//                if (ev.action == MotionEvent.ACTION_DOWN) {
//                    val v = currentFocus
//                    if (isShouldHideInput(v, ev)) {
//                        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//                        imm?.hideSoftInputFromWindow(v!!.windowToken, 0)
//                    }
//                    return super.dispatchTouchEvent(ev)
//                }
//                // Essential, otherwise all components will not have TouchEvent
//                return if (window!!.superDispatchTouchEvent(ev)) {
//                    true
//                } else onTouchEvent(ev)
//            }
//
////            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
////                if (ev.action == MotionEvent.ACTION_DOWN) {
////                    val v = currentFocus
////                    if (v is EditText || v is TextInputEditText|| v is TextInputLayout) {
////                        val outRect = Rect()
////                        v.getGlobalVisibleRect(outRect)
////                        if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
////                            v.clearFocus()
////                            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                            imm.hideSoftInputFromWindow(v.windowToken, 0)
////                        }
////                    }
////                }
////                setFullScreen()
////                return super.dispatchTouchEvent(ev)
////            }
//        }
//    }
//
//    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
//        if (v != null && (v is EditText || v is TextInputEditText || v is TextInputLayout)) {
//            val leftTop = intArrayOf(0, 0)
//            //Get the current location of the input box
//            v.getLocationInWindow(leftTop)
//            val left = leftTop[0]
//            val top = leftTop[1]
//            val bottom = top + v.getHeight()
//            val right = left + v.getWidth()
//            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
//        }
//        return false
//    }
//}