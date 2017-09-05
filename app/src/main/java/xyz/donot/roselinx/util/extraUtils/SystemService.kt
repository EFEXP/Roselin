package xyz.donot.roselinx.util.extraUtils

import android.accounts.AccountManager
import android.app.*
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothAdapter
import android.content.ClipboardManager
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.*
import android.os.storage.StorageManager
import android.print.PrintManager
import android.service.wallpaper.WallpaperService
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager

fun Context.getConnectivityManager(): ConnectivityManager =
        getSystemServiceAs(Context.CONNECTIVITY_SERVICE)

fun Context.getDownloadManager(): DownloadManager =
        getSystemServiceAs(Context.DOWNLOAD_SERVICE)

fun Context.getInputMethodManager(): InputMethodManager =
        getSystemServiceAs(Context.INPUT_METHOD_SERVICE)

fun Context.getNotificationManager(): NotificationManager =
        getSystemServiceAs(Context.NOTIFICATION_SERVICE)

fun Context.getTelephonyManager(): TelephonyManager =
        getSystemServiceAs(Context.TELEPHONY_SERVICE)

@Suppress("UNCHECKED_CAST")
fun <T> Context.getSystemServiceAs(serviceName: String): T =
        this.getSystemService(serviceName) as T
