package barlovinto.ricky.myslambook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import barlovinto.ricky.myslambook.databinding.ActivityForm3Binding
import barlovinto.ricky.myslambook.model.SlamBook
import coil.load
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Form3Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm3Binding
    private lateinit var slamBook: SlamBook
    private var currentPhotoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentPhotoUri != null) {
            binding.ivProfile.load(currentPhotoUri) { crossfade(true) }
        }
    }

    private val pickFromGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentPhotoUri = it
            binding.ivProfile.load(it) { crossfade(true) }
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        if (perms[Manifest.permission.CAMERA] == true) {
            dispatchTakePicture()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        slamBook = intent.getParcelableExtra("slamBook", SlamBook::class.java) ?: SlamBook()

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnTakePicture.setOnClickListener { onTakePictureClicked() }
        binding.btnBrowse.setOnClickListener { onBrowseClicked() }
        binding.btnBack.setOnClickListener { onSupportNavigateUp() }
        binding.btnSubmit.setOnClickListener { submitForm() }
    }

    private fun onTakePictureClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePicture()
        } else {
            requestPermissionsLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private fun onBrowseClicked() {
        pickFromGalleryLauncher.launch("image/*")
    }

    private fun submitForm() {
        val love = binding.etDefineLove.text.toString().trim()
        val friendship = binding.etDefineFriendship.text.toString().trim()
        val describe = binding.etDescribeMe.text.toString().trim()
        val advice = binding.etAdvice.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (love.isEmpty() || friendship.isEmpty() || describe.isEmpty() || advice.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        slamBook.apply {
            defineLove = love
            defineFriendship = friendship
            describeMe = describe
            adviceForMe = advice
            rateMe = rating.toInt()
        }

        showSummaryDialog()
    }

    private fun showSummaryDialog() {
        val summary = """
            Full Name: ${slamBook.firstName} ${slamBook.lastName}
            Nickname: ${slamBook.nickName}
            Birthdate: ${slamBook.birthDate}
            Gender: ${slamBook.gender}
            Status: ${slamBook.status}
            Email: ${slamBook.email}
            Contact: ${slamBook.contactNo}
            Address: ${slamBook.address}
            
            --- Favorites ---
            ${slamBook.favorites.joinToString("\n") { "- ${it.name} (${it.category})" }}
            
            --- Final Thoughts ---
            Define Love: ${slamBook.defineLove}
            Define Friendship: ${slamBook.defineFriendship}
            Describe Me: ${slamBook.describeMe}
            Advice: ${slamBook.adviceForMe}
            Rating: ${slamBook.rateMe} stars
            Image URI: $currentPhotoUri
            """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Slam Book Entry Summary")
            .setMessage(summary)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, MenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .show()
    }

    private fun dispatchTakePicture() {
        try {
            val photoFile = createImageFile()
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(currentPhotoUri)
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = cacheDir
        val imagesDir = File(storageDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", imagesDir)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onSupportNavigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, Form2Activity::class.java)
        intent.putExtra("slamBook", slamBook)
        startActivity(intent)
        finish()
        return true
    }
}