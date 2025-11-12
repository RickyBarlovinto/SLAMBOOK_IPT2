package barlovinto.ricky.myslambook

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import barlovinto.ricky.myslambook.databinding.FragmentFormPageOneBinding
import barlovinto.ricky.myslambook.model.SlamBook
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class FormPageOneFragment : Fragment() {

    private lateinit var binding: FragmentFormPageOneBinding
    private lateinit var slamBook: SlamBook

    // Sample existing emails for validation
    private val existingEmails = listOf("test@gmail.com", "hello@yahoo.com", "sample@outlook.com")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFormPageOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)

        slamBook = arguments?.getParcelable("slamBook") ?: SlamBook()

        setupAutoCompleteTextViews()
        setupBirthdatePicker()

        binding.ccp.registerCarrierNumberEditText(binding.contactNo)

        binding.btnNext.setOnClickListener { onNextClicked() }
        binding.btnBack.setOnClickListener { activity.onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupAutoCompleteTextViews() {
        val genderAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.gender)
        )
        binding.gender.setAdapter(genderAdapter)

        val statusAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            resources.getStringArray(R.array.status)
        )
        binding.status.setAdapter(statusAdapter)
    }

    private fun setupBirthdatePicker() {
        binding.birthdateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.birthdateInput.setText(formatted)
                    // Also update age display
                    val age = computeAge(selectedYear, selectedMonth, selectedDay)
                    binding.ageDisplay.text = "Age: $age"
                },
                year, month, day
            )

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNextClicked() {
        if (validateInputs()) {
            saveInputs()
            Toast.makeText(requireContext(), "Form validated successfully!", Toast.LENGTH_SHORT).show()
            val bundle = Bundle().apply {
                putParcelable("slamBook", slamBook)
            }
            findNavController().navigate(R.id.action_formPageOneFragment_to_formPageTwoFragment, bundle)
        } else {
            Toast.makeText(requireContext(), "Please correct the highlighted fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        clearAllErrors()
        var isValid = true

        fun requiredField(field: TextInputEditText, fieldName: String): Boolean {
            if (field.text.toString().trim().isEmpty()) {
                field.error = "$fieldName is required"
                return false
            }
            return true
        }

        if (!requiredField(binding.nickName, "Nickname")) isValid = false
        if (!requiredField(binding.lastName, "Lastname")) isValid = false
        if (!requiredField(binding.firstName, "Firstname")) isValid = false
        if (!requiredField(binding.emailAdd, "Email")) isValid = false
        if (!requiredField(binding.address, "Address")) isValid = false
        if (!requiredField(binding.birthdateInput, "Birthdate")) isValid = false

        // Email Validation (Format and Uniqueness)
        val email = binding.emailAdd.text.toString().trim()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAdd.error = "Invalid email format"
            isValid = false
        } else if (existingEmails.contains(email.lowercase())) {
            binding.emailAdd.error = "Email already in use"
            isValid = false
        }

        if (!validateBirthdate()) isValid = false
        
        if (!binding.ccp.isValidFullNumber) {
            binding.contactNo.error = "Invalid phone number"
            isValid = false
        }

        return isValid
    }

    private fun validateBirthdate(): Boolean {
        val birthdateStr = binding.birthdateInput.text.toString().trim()
        if (birthdateStr.isEmpty()) return true

        val parts = birthdateStr.split("-")
        if (parts.size != 3) {
            binding.birthdateInput.error = "Invalid date format (yyyy-MM-dd)"
            return false
        }

        val year = parts[0].toIntOrNull()
        val month = parts[1].toIntOrNull()
        val day = parts[2].toIntOrNull()

        if (year == null || month == null || day == null) {
            binding.birthdateInput.error = "Invalid date components"
            return false
        }

        val age = computeAge(year, month -1, day)
        if (age < 10) {
            binding.birthdateInput.error = "Must be at least 10 years old"
            return false
        }

        return true
    }
    
    private fun computeAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        dob.set(year, month, day)
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    private fun clearAllErrors() {
        binding.apply {
            nickName.error = null
            lastName.error = null
            firstName.error = null
            emailAdd.error = null
            address.error = null
            contactNo.error = null
            birthdateInput.error = null
        }
    }

    private fun saveInputs() {
        slamBook.apply {
            nickName = binding.nickName.text.toString().trim()
            friendCallMe = binding.friendCall.text.toString().trim()
            likeToCallMe = binding.likeToCall.text.toString().trim()
            lastName = binding.lastName.text.toString().trim()
            firstName = binding.firstName.text.toString().trim()
            birthDate = binding.birthdateInput.text.toString().trim()
            gender = binding.gender.text.toString()
            status = binding.status.text.toString()
            email = binding.emailAdd.text.toString().trim()
            contactNo = binding.ccp.fullNumberWithPlus.trim()
            address = binding.address.text.toString().trim()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressedDispatcher?.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}