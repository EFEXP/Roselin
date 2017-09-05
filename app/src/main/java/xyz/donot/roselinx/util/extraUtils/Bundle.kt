package xyz.donot.roselinx.util.extraUtils
import android.os.Bundle

inline fun Bundle(body: Bundle.() -> Unit): Bundle {
    val bundle = Bundle()
    bundle.body()
    return bundle
}

inline fun Bundle(loader: ClassLoader, body: Bundle.() -> Unit): Bundle {
    val bundle = Bundle(loader)
    bundle.body()
    return bundle
}

inline fun Bundle(capacity: Int, body: Bundle.() -> Unit): Bundle {
    val bundle = Bundle(capacity)
    bundle.body()
    return bundle
}

inline fun Bundle(b: Bundle?, body: Bundle.() -> Unit): Bundle {
    val bundle = Bundle(b)
    bundle.body()
    return bundle
}
