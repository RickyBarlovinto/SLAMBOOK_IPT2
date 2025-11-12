package barlovinto.ricky.myslambook

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import barlovinto.ricky.myslambook.databinding.FragmentFormPageThreeBinding
import barlovinto.ricky.myslambook.model.SlamBook
import coil.load
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FormPageThreeFragment : Fragment() {

    private lateinit var binding: FragmentFormPageThreeBinding
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
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormPageThreeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slamBook = arguments?.getParcelable("slamBook") ?: SlamBook()

        setupToolbar()
        setupButtons()
    }

    private fun setupToolbar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupButtons() {
        binding.btnTakePicture.setOnClickListener { onTakePictureClicked() }
        binding.btnBrowse.setOnClickListener { onBrowseClicked() }
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnSubmit.setOnClickListener { submitForm() }
    }

    private fun onTakePictureClicked() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        slamBook.apply {
            defineLove = love
            defineFriendship = friendship
            describeMe = describe
            adviceForMe = advice
            rateMe = rating.toInt()
        }

        showSummaryDialogAndSave()
    }

    private fun showSummaryDialogAndSave() {
        val summary = buildString {
            append("Full Name: ${slamBook.firstName} ${slamBook.lastName}\n")
            append("Nickname: ${slamBook.nickName}\n")
            append("Age: ${slamBook.birthDate?.let { computeAge(it).toString() + " years old" } ?: "N/A"}\n")
            append("Gender: ${slamBook.gender}\n")
            append("Status: ${slamBook.status}\n")
            append("Email: ${slamBook.email}\n")
            append("Contact: ${slamBook.contactNo}\n")
            append("Address: ${slamBook.address}\n\n")
            append("--- Favorites ---\n")
            slamBook.favorites.forEach { append("- ${it.name} (${it.category})\n") }
            append("\n--- Final Thoughts ---\n")
            append("Define Love: ${slamBook.defineLove}\n")
            append("Define Friendship: ${slamBook.defineFriendship}\n")
            append("Describe Me: ${slamBook.describeMe}\n")
            append("Advice: ${slamBook.adviceForMe}\n")
            append("Rating: ${slamBook.rateMe} stars\n")
            append("Image URI: $currentPhotoUri")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Slam Book Entry Summary")
            .setMessage(summary)
            .setPositiveButton("Confirm & Save") { dialog, _ ->
                saveEntryToPreferences(summary)
                dialog.dismiss()
                val intent = Intent(requireActivity(), MenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveEntryToPreferences(entry: String) {
        val sharedPref = requireActivity().getSharedPreferences("SlamBookEntries", Context.MODE_PRIVATE)
        val entries = sharedPref.getStringSet("entries", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        entries.add(entry) // Add the new entry
        sharedPref.edit().putStringSet("entries", entries).apply()
    }

    private fun computeAge(birthdate: String): Int? {
        return try {
            val parts = birthdate.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            val dob = Calendar.getInstance()
            dob.set(year, month - 1, day)
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            null
        }
    }

    private fun dispatchTakePicture() {
        try {
            val photoFile = createImageFile()
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                photoFile
            )
            takePictureLauncher.launch(currentPhotoUri)
        } catch (ex: IOException) {
            Toast.makeText(requireContext(), "Error creating image: ${ex.message}", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = requireActivity().cacheDir
        val imagesDir = File(storageDir, "images")
        if (!imagesDir.exists()) imagesDir.mkdirs()
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", imagesDir)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            findNavController().navigateUp()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}