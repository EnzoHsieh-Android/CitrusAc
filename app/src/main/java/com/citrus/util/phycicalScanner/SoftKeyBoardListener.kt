package com.citrus.util.phycicalScanner

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener


class SoftKeyBoardListener(val activity: Activity) {
    private var rootView: View? = null
    var rootViewVisibleHeight = 0
    private var onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener? = null

    init {
        rootView = activity.window.decorView
        rootView?.viewTreeObserver?.addOnGlobalLayoutListener(OnGlobalLayoutListener { //获取当前根视图在屏幕上显示的大小
            val r = Rect()
            rootView?.getWindowVisibleDisplayFrame(r)
            val visibleHeight: Int = r.height()
            println("" + visibleHeight)
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            //根視圖顯示高度沒有變化，可以看作軟鍵盤顯示／隱藏狀態沒有改變
            if (rootViewVisibleHeight == visibleHeight) {
                return@OnGlobalLayoutListener
            }

            //根視圖顯示高度變小超過300，可以看作軟鍵盤顯示了，該數值可根據需要自行調整
            if (rootViewVisibleHeight - visibleHeight > 200) {
                onSoftKeyBoardChangeListener?.keyBoardShow(rootViewVisibleHeight - visibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }

            //根視圖顯示高度變大超過300，可以看作軟鍵盤隱藏了，該數值可根據需要自行調整
            if (visibleHeight - rootViewVisibleHeight > 200) {
                onSoftKeyBoardChangeListener?.keyBoardHide(visibleHeight - rootViewVisibleHeight)
                rootViewVisibleHeight = visibleHeight
                return@OnGlobalLayoutListener
            }
        })
    }

    private fun setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener
    }

    interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow(height: Int)
        fun keyBoardHide(height: Int)
    }

    fun setListener(onSoftKeyBoardChangeListener: OnSoftKeyBoardChangeListener) {
        val softKeyBoardListener = SoftKeyBoardListener(activity)
        softKeyBoardListener.setOnSoftKeyBoardChangeListener(onSoftKeyBoardChangeListener)
    }
}