package com.letter.cameraassistant.ui.fragment

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.core.content.edit
import androidx.preference.*

import com.letter.cameraassistant.R
import com.letter.cameraassistant.service.CoreService

private const val TAG = "SettingsFragment"

/**
 * 设置Fragment
 * @author Letter(nevermindzzt@gmail.com)
 * @since 1.0.0
 */
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onResume() {
        super.onResume()
        initCanOverlays()
        initAccessibilityPermission()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "enable_overlay_permission" -> {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data = Uri.parse("package:${requireContext().packageName}")
                startActivity(intent)
                return true
            }
            "enable_accessibility_permission" -> {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }

    private fun initCanOverlays() {
        val canOverlays = Settings.canDrawOverlays(requireContext())
        if (PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean("enable_overlay_permission", false) != canOverlays) {
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit {
                    putBoolean("enable_overlay_permission", canOverlays)
                }
            preferenceScreen
        }
    }

    private fun initAccessibilityPermission(): Boolean {
        val accessibilityManager = requireContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val activityManager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = activityManager.getRunningServices(100)
        runningServices.forEach {
            if (it.service.className == CoreService::class.java.name) {
                return accessibilityManager.isEnabled
            }
        }
        return false
    }
}