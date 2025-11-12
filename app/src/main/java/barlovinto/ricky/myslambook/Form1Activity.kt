package barlovinto.ricky.myslambook

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import barlovinto.ricky.myslambook.databinding.ActivityForm1Binding
import barlovinto.ricky.myslambook.model.SlamBook
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class Form1Activity : AppCompatActivity() {

    private lateinit var binding: ActivityForm1Binding
    private lateinit var slamBook: SlamBook

    private val existingEmails = listOf("test@gmail.com", "hello@yahoo.com", "sample@outlook.com")

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForm1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        slamBook = intent.getParcelableExtra("slamBook", SlamBook::class.java) ?: SlamBook()

        setupAutoCompleteTextViews()
        setupBirthdatePicker()

        binding.ccp.registerCarrierNumberEditText(binding.contactNo)

        binding.btnNext.setOnClickListener { onNextClicked() }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupAutoCompleteTextViews() {
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.gender))
        binding.gender.setAdapter(genderAdapter)

        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.status))
        binding.status.setAdapter(statusAdapter)
    }

    private fun setupBirthdatePicker() {
        binding.birthdateInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                binding.birthdateInput.setText(formattedDate)
                val age = computeAge(selectedYear, selectedMonth, selectedDay)
                binding.ageDisplay.text = "Age: $age"
            }, year, month, day).apply {
                datePicker.maxDate = System.currentTimeMillis()
                show()
            }
        }
    }

    private fun onNextClicked() {
        if (validateInputs()) {
            saveInputs()
            Toast.makeText(this, "Page 1 Validated!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Form2Activity::class.java)
            intent.putExtra("slamBook", slamBook)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Please correct the highlighted fields.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        fun required(field: TextInputEditText, name: String): Boolean {
            if (field.text.toString().trim().isEmpty()) {
                field.error = "$name is required"
                return false
            }
            return true
        }

        if (!required(binding.nickName, "Nickname")) isValid = false
        if (!required(binding.lastName, "Lastname")) isValid = false
        if (!required(binding.firstName, "Firstname")) isValid = false
        if (!required(binding.birthdateInput, "Birthdate")) isValid = false
        if (!required(binding.emailAdd, "Email")) isValid = false
        if (!required(binding.address, "Address")) isValid = false

        val email = binding.emailAdd.text.toString().trim()
        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailAdd.error = "Invalid email format"
            isValid = false
        } else if (existingEmails.contains(email.lowercase())) {
            binding.emailAdd.error = "Email already in use"
            isValid = false
        }

        if (!binding.ccp.isValidFullNumber) {
            binding.contactNo.error = "Invalid phone number"
            isValid = false
        }
        
        if(!validateBirthdate()) isValid = false

        return isValid
    }
    
    private fun validateBirthdate(): Boolean {
        val birthdateStr = binding.birthdateInput.text.toString().trim()
        if(birthdateStr.isEmpty()) return false
        
        val parts = birthdateStr.split("-")
        if (parts.size != 3) return false

        val year = parts[0].toIntOrNull()
        val month = parts[1].toIntOrNull()
        val day = parts[2].toIntOrNull()
        
        if(year == null || month == null || day == null) return false

        val age = computeAge(year, month - 1, day)
        if (age < 10) {
            binding.birthdateInput.error = "Must be at least 10 years old"
            return false
        }
        return true
    }

    private fun computeAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance().apply { set(year, month, day) }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
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
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}