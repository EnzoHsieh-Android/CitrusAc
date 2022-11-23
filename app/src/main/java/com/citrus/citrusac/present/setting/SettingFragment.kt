package com.citrus.citrusac.present.setting

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.citrus.citrusac.BuildConfig
import com.citrus.citrusac.databinding.FragmentSettingBinding
import com.citrus.citrusac.present.main.SharedViewModel
import com.citrus.di.prefs
import com.citrus.util.Constants
import com.citrus.util.apkDownload.DownloadStatus
import com.citrus.util.base.BaseDialogFragment
import com.citrus.util.ext.showErrDialog
import com.citrus.util.onSafeClick
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@AndroidEntryPoint
class SettingFragment :
    BaseDialogFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate, true) {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var mProgressDialog: ProgressDialog
    private val requestStorageCode = 888
    var downloadTempVer = ""

    @SuppressLint("SetTextI18n")
    override fun initView() {
        isCancelable = false

        binding.apply {
            initProgressDialog()
            etServerIp.setText(prefs.serverIp)
            etDeviceId.setText(prefs.deviceId)

            tvVersion.text =
                "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})${if (BuildConfig.DEBUG) " - DEBUG" else ""}"

            llCheck.onSafeClick {
                if (etServerIp.text.isBlank()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(etServerIp)
                    return@onSafeClick
                } else {
                    prefs.serverIp = etServerIp.text.trim().toString().replace(" ", "")
                }

                if (etDeviceId.text.isBlank()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(etDeviceId)
                    return@onSafeClick
                } else {
                    prefs.deviceId = etDeviceId.text.trim().toString().replace(" ", "")
                }

                dismiss()
            }


            tvVersion.onSafeClick {
                val permissionCheck =
                    activity?.let { it1 ->
                        ContextCompat.checkSelfPermission(
                            it1,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    }

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    activity?.let { it1 ->
                        ActivityCompat.requestPermissions(
                            it1,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            requestStorageCode
                        )
                    }
                } else {
                    updateDialog()
                }
            }
        }
    }

    override fun initAction() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            sharedViewModel.downloadStatus.collectLatest { event ->
                when (event) {
                    is DownloadStatus.Success -> {
                        mProgressDialog.dismiss()

                        val apkUri = FileProvider.getUriForFile(
                            requireActivity(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            getFile()
                        )
                        val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
                        install.data = apkUri
                        install.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        startActivity(install)
                        downloadTempVer = ""
                    }
                    is DownloadStatus.Error -> {
                        mProgressDialog.dismiss()
                        showErrDialog(
                            title = "與系統連線發生問題",
                            msg = "下載失敗",
                            onConfirmListener = {
                                downloadApk(downloadTempVer)
                            }
                        )
                    }
                    is DownloadStatus.Progress -> {
                        mProgressDialog.isIndeterminate = false
                        mProgressDialog.progress = event.progress
                    }
                }
            }
        }
    }

    override fun clearMemory() = Unit

    private fun updateDialog() {
        CustomDownloadDialog(requireContext(),
            onConfirmListener = { versionStr ->
                downloadApk(versionStr)
            }).show()
    }

    private fun downloadApk(name: String) {
        if (name == BuildConfig.VERSION_NAME) {
            Toast.makeText(activity, "已是最新版本，無需更新", Toast.LENGTH_SHORT).show()
            return
        }
        downloadTempVer = name
        sharedViewModel.intentUpdate(
            getFile(),
            Constants.DOWNLOAD_URL + "citrusAc_v$name.apk"
        )
        mProgressDialog.show()
    }

    private fun getFile(): File {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "citrusAc.apk"
            )
        } else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "citrusAc.apk"
            )
        }
    }

    private fun initProgressDialog() {
        mProgressDialog = ProgressDialog(requireActivity())
        mProgressDialog.setMessage("下載任務進行中..")
        mProgressDialog.isIndeterminate = true
        mProgressDialog.max = 100
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(false)
        mProgressDialog.setButton(
            Dialog.BUTTON_NEGATIVE,
            "取消"
        ) { _: DialogInterface?, _: Int ->
            sharedViewModel.cancelUpdateJob()
        }
    }

}