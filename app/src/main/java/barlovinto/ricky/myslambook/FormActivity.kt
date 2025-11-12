package barlovinto.ricky.myslambook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import barlovinto.ricky.myslambook.databinding.ActivityForm1Binding
import barlovinto.ricky.myslambook.databinding.ActivityFormBinding
import barlovinto.ricky.myslambook.model.SlamBook

class FormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormBinding
    private lateinit var slamBook: SlamBook

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponent()
    }

    private fun initComponent() {

    }

}