package ru.smasung.itacademy.mdev.defaultcameraappexample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private var button: Button? = null
    private var videoView: VideoView? = null
    private var videoFilePath = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button)
        videoView = findViewById(R.id.video)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION)
        }
        button?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                openCameraIntent()
            }
        })
    }

    private fun openCameraIntent() {
        val videoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        Log.d("MAIN_LOG", videoIntent.resolveActivity(packageManager).toString())
        if (videoIntent.resolveActivity(packageManager) != null) {
            var videoFile: File? = null
            videoFile = try {
                createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            val videoUri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", videoFile)
            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
            Log.d("MAIN_LOG", videoUri.toString())
            startActivityForResult(videoIntent, REQUEST_VIDEO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO) {
            if (resultCode == Activity.RESULT_OK) {
                videoView?.setVideoURI(Uri.parse(videoFilePath))
                videoView?.start()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val videoFileName = "VIDEO_" + timeStamp + "_"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        val video: File = File.createTempFile(videoFileName, ".mp4", storageDir)
        videoFilePath = video.absolutePath
        return video
    }

    companion object {
        const val REQUEST_VIDEO = 100
        const val REQUEST_PERMISSION = 200
    }
}