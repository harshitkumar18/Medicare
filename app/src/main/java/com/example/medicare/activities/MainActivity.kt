package com.example.medicare.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment

import com.example.medicare.R
import com.example.medicare.databinding.ActivityMainBinding
import com.example.medicare.databinding.ActivityResetPasswordBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.sql.Types.NULL

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var bottomNav: BottomNavigationView? = null
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        bottomNav = findViewById(R.id.bottomNav)
        binding?.navView?.setNavigationItemSelectedListener(this)

        bottomNav?.setOnNavigationItemSelectedListener { item ->
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
        setupActionBar()
    }
    private fun setupActionBar(){
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_main_activity)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener{
            toggleDrawer()
        }

    }
    private fun toggleDrawer(){
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout!!.openDrawer(GravityCompat.START)
        }
    }
    override fun onBackPressed() {
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout!!.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
//                startActivityForResult(Intent(this,MyProfileActivity::class.java),
//                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out ->{
//                FirebaseAuth.getInstance().signOut()
//                mSharedPreferences.edit().clear().apply()
//
//                val intent = Intent(this,IntroActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//                startActivity(intent)
//                finish()
            }

        }
        binding?.drawerLayout!!.closeDrawer(GravityCompat.START)

        return true

    }



}