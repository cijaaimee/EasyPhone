package es.cinsua.easyphone.app.ui.launcher

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val BASE_PACKAGE_NAME = "es.cinsua.easyphone"

data class AppInfo(val label: String, val packageName: String, val icon: Drawable)

data class LauncherUiState(val apps: List<AppInfo> = emptyList(), val isLoading: Boolean = true)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(LauncherUiState())
  val uiState = _uiState.asStateFlow()

  private val packageChangeReceiver =
      object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
          loadInstalledApps()
        }
      }

  init {
    loadInstalledApps()

    val intentFilter =
        IntentFilter().apply {
          addAction(Intent.ACTION_PACKAGE_ADDED)
          addAction(Intent.ACTION_PACKAGE_REMOVED)
          addAction(Intent.ACTION_PACKAGE_CHANGED)
          addDataScheme("package")
        }
    getApplication<Application>().registerReceiver(packageChangeReceiver, intentFilter)
  }

  override fun onCleared() {
    super.onCleared()
    getApplication<Application>().unregisterReceiver(packageChangeReceiver)
  }

  private fun loadInstalledApps() {
    viewModelScope.launch(Dispatchers.IO) {
      _uiState.update { it.copy(isLoading = true) }

      val application = getApplication<Application>()
      val packageManager: PackageManager = application.packageManager
      val mainIntent =
          Intent(Intent.ACTION_MAIN, null).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
      val allLaunchableActivities = packageManager.queryIntentActivities(mainIntent, 0)

      val uniquePackageNames = allLaunchableActivities.map { it.activityInfo.packageName }.toSet()

      val apps =
          uniquePackageNames
              .mapNotNull { packageName ->
                packageManager.getLaunchIntentForPackage(packageName)?.let { intent ->
                  packageManager.resolveActivity(intent, 0)?.let { resolveInfo ->
                    AppInfo(
                        label = resolveInfo.loadLabel(packageManager).toString(),
                        packageName = resolveInfo.activityInfo.packageName,
                        icon = resolveInfo.loadIcon(packageManager))
                  }
                }
              }
              .filterNot { it.packageName.startsWith(BASE_PACKAGE_NAME) }
              .sortedBy { it.label.lowercase() }
              .toList()

      _uiState.update { it.copy(apps = apps, isLoading = false) }
    }
  }
}
