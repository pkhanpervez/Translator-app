package com.englishto.urdu.translator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.englishto.urdu.translator.databinding.ActivityMainBinding
import com.englishto.urdu.translator.interfaces.LanguageDownloadListener
import com.englishto.urdu.translator.prefrence.SharedPref

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, LanguageDownloadListener  {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var isHorizontal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lastOrientation = SharedPref.getInstance(this@MainActivity).getFromSession("IS_HORIZONTAL", "false")

        if(lastOrientation.equals("true")) {
            isHorizontal = true
        } else {
            isHorizontal = false
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_ourapp,
                R.id.nav_share,  // Optional
                R.id.nav_rateus

            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener(this)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_direction -> {

                isHorizontal = !isHorizontal
                if(isHorizontal) {
                    item.setIcon(R.drawable.baseline_vertical_distribute_24) // Replace with your desired icon
                } else {
                    item.setIcon(R.drawable.baseline_horizontal_distribute_24) // Replace with your desired icon
                }

            }
        }

        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar_direction_menu, menu)

        if(isHorizontal) {
            menu.findItem(R.id.action_direction)?.setIcon(R.drawable.baseline_vertical_distribute_24)
        } else {
            menu.findItem(R.id.action_direction)?.setIcon(R.drawable.baseline_horizontal_distribute_24)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_ourapp -> {
                val developerName = "AZ360 Soft"  // Replace with the exact developer name
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=$developerName"))
                startActivity(intent)
            }
            R.id.nav_share -> {
                val appName = getString(R.string.app_name)
                val str =
                    """${
                        """You can download $appName app from the given below Link:
https://play.google.com/store/apps/details?id=$packageName"""
                    }

        Thank You"""
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, str)
                startActivity(Intent.createChooser(shareIntent, "Share Link"))
            }
            R.id.nav_rateus -> {
               startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                ))
            }
        }

        // Close the drawer
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun updateDownloadedStatus(boolean: Boolean) {

    }
}