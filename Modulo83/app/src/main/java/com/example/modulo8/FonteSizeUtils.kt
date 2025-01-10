package com.example.modulo8

import android.view.ViewGroup
import android.widget.TextView

object FontSizeManager {
    var fontSize: Int = 16 // Valor inicial do tamanho da fonte

    fun updateFontSize(viewGroup: ViewGroup) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) {
                child.textSize = fontSize.toFloat()
            } else if (child is ViewGroup) {
                updateFontSize(child) // Recursivamente altera os filhos
            }
        }
    }
}
