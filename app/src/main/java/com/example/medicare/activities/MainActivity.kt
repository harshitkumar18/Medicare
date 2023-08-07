package com.example.medicare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.example.medicare.R
import com.example.medicare.databinding.ActivityMainBinding
import com.example.medicare.databinding.ActivityResetPasswordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.sql.Types.NULL

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)


        binding?.bottomNav?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
//                    intent(this,HomeA)
                    true
                }
                R.id.message -> {
//                    loadFragment(ChatFragment())
                    true
                }
                R.id.settings -> {
//                    loadFragment(SettingFragment())
                       val  intent = Intent(this,SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
            true
        }
    }


}