package com.mraha.imagesearchapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment

class ItemClickedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val position = arguments?.getInt("position", -1)
        val array = resources.getStringArray(R.array.dialog_actions)
        val builder = AlertDialog.Builder(requireContext())
        builder.setItems(
            array
        ) { dialog, which ->
            when (array[which]) {
                getString(R.string.save_cat_to_favorites) -> {
                    Toast.makeText(
                        requireContext(),
                        "pos" + position, Toast.LENGTH_SHORT
                    ).show()
                }
                getString(R.string.save_cat_to_downloads) -> {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            resources.getInteger(R.integer.write_to_storage_request)
                        )
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "access ", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return builder.create()
    }
}