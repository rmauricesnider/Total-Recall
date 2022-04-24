package com.example.totalrecall

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.totalrecall.databinding.ActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBinding

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_host)
        val navController: NavController = navHostFragment!!.findNavController()

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(toolbar, navController)
    }
}