package com.citrus.util.phycicalScanner

import android.view.KeyEvent

class ScanKeyManager(listener: OnScanValueListener) {
    private var mResult: StringBuilder = StringBuilder()
    var mListener: OnScanValueListener? = null
    private var mCaps = false

    interface OnScanValueListener {
        fun onScanValue(value: String?)
    }

    init {
        mListener = listener
    }


    fun analysisKeyEvent(event: KeyEvent) {
        val keyCode: Int = event.keyCode
        checkLetterStatus(event)
        if (event.action == KeyEvent.ACTION_DOWN) {
            val aChar = getInputCode(mCaps, event.keyCode)
            if (aChar.code != 0) {
                mResult.append(aChar)
            }
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                mListener?.onScanValue(mResult.toString())
                mResult.delete(0, mResult.length)
            }
        }
    }


    private fun checkLetterStatus(event: KeyEvent) {
        val keyCode: Int = event.keyCode
        if (keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT) {
            mCaps = event.action == KeyEvent.ACTION_DOWN
        }
    }

    /**
     * 將keyCode轉為char
     *
     * @param caps    是不是大寫
     * @param keyCode 按鍵
     * @return 按鍵對應的char
     */
    private fun getInputCode(caps: Boolean, keyCode: Int): Char {
        return if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            ((if (caps) 'A' else 'a').code + keyCode - KeyEvent.KEYCODE_A).toChar()
        } else {
            keyValue(caps, keyCode)
        }
    }

    /**
     * 按鍵對應的Char表
     */
    private fun keyValue(caps: Boolean, keyCode: Int): Char {
        return when (keyCode) {
            KeyEvent.KEYCODE_0 -> if (caps) ')' else '0'
            KeyEvent.KEYCODE_1 -> if (caps) '!' else '1'
            KeyEvent.KEYCODE_2 -> if (caps) '@' else '2'
            KeyEvent.KEYCODE_3 -> if (caps) '#' else '3'
            KeyEvent.KEYCODE_4 -> if (caps) '$' else '4'
            KeyEvent.KEYCODE_5 -> if (caps) '%' else '5'
            KeyEvent.KEYCODE_6 -> if (caps) '^' else '6'
            KeyEvent.KEYCODE_7 -> if (caps) '&' else '7'
            KeyEvent.KEYCODE_8 -> if (caps) '*' else '8'
            KeyEvent.KEYCODE_9 -> if (caps) '(' else '9'
            KeyEvent.KEYCODE_NUMPAD_SUBTRACT -> '-'
            KeyEvent.KEYCODE_MINUS -> '_'
            KeyEvent.KEYCODE_EQUALS -> '='
            KeyEvent.KEYCODE_NUMPAD_ADD -> '+'
            KeyEvent.KEYCODE_GRAVE -> if (caps) '~' else '`'
            KeyEvent.KEYCODE_BACKSLASH -> if (caps) '|' else '\\'
            KeyEvent.KEYCODE_LEFT_BRACKET -> if (caps) '{' else '['
            KeyEvent.KEYCODE_RIGHT_BRACKET -> if (caps) '}' else ']'
            KeyEvent.KEYCODE_SEMICOLON -> if (caps) ':' else ';'
            KeyEvent.KEYCODE_APOSTROPHE -> if (caps) '"' else '\''
            KeyEvent.KEYCODE_COMMA -> if (caps) '<' else ','
            KeyEvent.KEYCODE_PERIOD -> if (caps) '>' else '.'
            KeyEvent.KEYCODE_SLASH -> if (caps) '?' else '/'
            else -> 0.toChar()
        }
    }
}