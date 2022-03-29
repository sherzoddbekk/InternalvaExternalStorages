import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.internalvaexternalstorages.R
import java.io.*
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    var isPersistant: Boolean = true
    var isInternal: Boolean = true
    var readPermissionGranted: Boolean = false
    var writePermissionGranted: Boolean = false
    lateinit var btn_save_internal: Button
    lateinit var btn_read_internal: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        checkStoragePaths()
        requestPermission()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createInternalFile()
        initViews()
    }

    private fun initViews() {
        btn_save_internal = findViewById(R.id.save_int)
        btn_read_internal = findViewById(R.id.read_int)
        btn_save_internal.setOnClickListener {
            saveInternalFile("Sarvar")
        }
        btn_read_internal.setOnClickListener {
            readInternalFile()
        }
    }

    private fun readInternalFile() {
        val filename = "pdp_internal.txt"
        try {
            val fileInputStream: FileInputStream
            fileInputStream = if (isPersistant) {
                openFileInput(filename)
            } else {
                val file = File(cacheDir, filename)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("/n", lines)
            Toast.makeText(this, "read from  %fileName successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "read from  %fileName failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createInternalFile() {
        val fileName = "pdp_internal.txt"
        val file: File
        file = if (isPersistant) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this, "FIle %fileName has been created", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "FIle %s creation failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "FIle %s already exists", Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkStoragePaths() {
        val internal_m1 = getDir("custom", 0)
        val internal_m2 = filesDir

        val external_m1 = getExternalFilesDir(null)
        val external_m2 = externalCacheDir
        val external_m3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)


        Log.d("StorageActivity", internal_m1.absolutePath)
        Log.d("StorageActivity", internal_m2.absolutePath)
        Log.d("StorageActivity", external_m1!!.absolutePath)
        Log.d("StorageActivity", external_m2!!.absolutePath)
        Log.d("StorageActivity", external_m3!!.absolutePath)
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        try {
            val fileOutputStream: FileOutputStream
            fileOutputStream = if (isPersistant) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, "Write to  %fileName successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Write to  %fileName failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val misSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || misSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!readPermissionGranted)
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        if (!writePermissionGranted)
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionsToRequest.isNotEmpty())
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if (readPermissionGranted) Toast.makeText(
                this,
                "READ_EXTERNAL_STORAGE",
                Toast.LENGTH_SHORT
            ).show()
            if (writePermissionGranted) Toast.makeText(
                this,
                "WRITE_EXTERNAL_STORAGE",
                Toast.LENGTH_SHORT
            ).show()


        }


    //external
    private fun readExternalFile() {
        val filename = "pdp_internal.txt"
        val file:File
        file = if (isPersistant)
            File(getExternalFilesDir(null),filename)
        else
            File(externalCacheDir,filename)
        try {
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("/n", lines)
            Toast.makeText(this, "read from  %fileName successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "read from  %fileName failed", Toast.LENGTH_SHORT).show()
        }
    }



    private fun saveExternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        val file:File
        file = if (isPersistant)
            File(getExternalFilesDir(null),fileName)
        else
            File(externalCacheDir,fileName)
        try {
            val fileOutputStream =  FileOutputStream(file)

            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this, "Write to  %fileName successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Write to  %fileName failed", Toast.LENGTH_SHORT).show()
        }
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
        bitmap ->
        val fileName = UUID.randomUUID().toString()
        val isPhotoSaved = if (isInternal){
            savePhotoToInternalStorage(fileName,bitmap!!)
        }else{
            if (writePermissionGranted){
                savePhotoToExternalStorage(fileName,bitmap!!)
            }else{
                false
            }
        }
        if (isPhotoSaved){
            Toast.makeText(this,"Photo saved successfully",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"Failed to sacve photo",Toast.LENGTH_SHORT).show()
        }
    }
}