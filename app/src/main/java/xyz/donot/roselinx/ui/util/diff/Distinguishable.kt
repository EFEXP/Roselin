package xyz.donot.roselinx.ui.util.diff

interface Distinguishable {
    fun isTheSame(other: Distinguishable): Boolean
    fun isContentsTheSame(other: Distinguishable): Boolean = equals(other)
}

