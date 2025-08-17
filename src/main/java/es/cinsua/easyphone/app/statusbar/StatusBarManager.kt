package es.cinsua.easyphone.app.statusbar

import android.app.StatusBarManager

const val DISABLE_EXPAND = 0x00010000
const val DISABLE_NOTIFICATION_ICONS = 0x00020000
const val DISABLE_NOTIFICATION_ALERTS = 0x00040000
const val DISABLE_SYSTEM_INFO = 0x00100000
const val DISABLE_HOME = 0x00200000
const val DISABLE_RECENT = 0x01000000
const val DISABLE_BACK = 0x00400000
const val DISABLE_CLOCK = 0x00800000
const val DISABLE_SEARCH = 0x02000000
const val DISABLE_ONGOING_CALL_CHIP = 0x04000000
const val DISABLE_NONE = 0x00000000

const val DISABLE2_QUICK_SETTINGS = 1
const val DISABLE2_SYSTEM_ICONS = 1 shl 1
const val DISABLE2_NOTIFICATION_SHADE = 1 shl 2
const val DISABLE2_GLOBAL_ACTIONS = 1 shl 3
const val DISABLE2_ROTATE_SUGGESTIONS = 1 shl 4
const val DISABLE2_NONE = 0x00000000

fun StatusBarManager.disable(disable: Int) {
  val method = StatusBarManager::class.java.getDeclaredMethod("disable", Int::class.java)
  method.isAccessible = true
  method.invoke(this, disable)
}

fun StatusBarManager.disable2(disable: Int) {
  val method = StatusBarManager::class.java.getDeclaredMethod("disable2", Int::class.java)
  method.isAccessible = true
  method.invoke(this, disable)
}
