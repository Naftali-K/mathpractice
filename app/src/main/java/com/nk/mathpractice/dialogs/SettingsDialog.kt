package com.nk.mathpractice.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.nk.mathpractice.R
import com.nk.mathpractice.databinding.DialogSettingsBinding

class SettingsDialog : DialogFragment() {

    interface CallbackListener {
        fun onSettingsConfirmed(operations: List<String>, digits: Int, allowNegative: Boolean)
    }

    private var callback: CallbackListener? = null

    fun setCallback(listener: CallbackListener) {
        this.callback = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSettingsBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.settings_title)
            .setView(binding.root)
            .setCancelable(false)
            .setPositiveButton(R.string.start_button, null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val operations = mutableListOf<String>()
                if (binding.cbPlus.isChecked) operations.add("+")
                if (binding.cbMinus.isChecked) operations.add("-")
                if (binding.cbMultiply.isChecked) operations.add("*")
                if (binding.cbDivide.isChecked) operations.add("/")
                if (binding.cbMod.isChecked) operations.add("%")

                if (operations.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.error_select_operation, Toast.LENGTH_SHORT).show()
                } else {
                    val digits = when (binding.rgDigits.checkedRadioButtonId) {
                        R.id.rb2Digits -> 2
                        R.id.rb3Digits -> 3
                        else -> 1
                    }
                    val allowNegative = binding.cbAllowNegative.isChecked
                    callback?.onSettingsConfirmed(operations, digits, allowNegative)
                    dismiss()
                }
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "SettingsDialog"
    }
}
