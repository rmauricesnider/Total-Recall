package com.example.totalrecall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.totalrecall.databinding.ActivityBinding

private const val SHARE_LINK = "SHARE_LINK"

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


        if(intent?.action == Intent.ACTION_SEND) {
            val bundle = Bundle()
            val s = intent.getStringExtra(Intent.EXTRA_TEXT)
            bundle.putString(SHARE_LINK, s)
            navController.navigate(R.id.list_to_add, bundle)
        }
    }
}