package com.letter.cameraassistant.ui.dialog

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.letter.cameraassistant.databinding.DialogAssistantConfigBinding
import com.letter.cameraassistant.model.bean.AssistantConfigData

typealias AssistantConfigCallback =((MaterialDialog, AssistantConfigData) -> Unit)

fun MaterialDialog.assistantConfig(
    callback: AssistantConfigCallback? = null
): MaterialDialog {

    val binding = DialogAssistantConfigBinding.inflate(layoutInflater)
    customView(view = binding.root, noVerticalPadding = true)
    binding.positiveButton.setOnClickListener {
        val configData = AssistantConfigData(
            textToInt(binding.photosIntervalInput.text.toString()),
            textToInt(binding.photosTimeInput.text.toString()),
            binding.intelligenceModeButton.isChecked
        )

        dismiss()
        callback?.invoke(this, configData)
    }
    return this
}

private fun textToInt(value: String): Int {
    return if (value.isNotEmpty()) {
        value.toInt()
    } else {
        0
    }
}
