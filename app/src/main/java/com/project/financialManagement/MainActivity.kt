package com.project.financialManagement

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.project.financialManagement.activity.AddTransactionActivity
import com.project.financialManagement.activity.SigninActivity
import com.project.financialManagement.databinding.ActivityMainBinding
import com.project.financialManagement.fragment.HomeFragment
import com.project.financialManagement.helper.SharedPreferencesHelper
import com.project.financialManagement.model.LanguageModel
import com.project.financialManagement.service.BudgetReminderService
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = SharedPreferencesHelper(this)
        val currentLanguage = LanguageModel.values().first { it.position == sharedPreferences.getLangPosition() }
        setLocale(this, currentLanguage.code)

        binding = ActivityMainBinding.inflate(layoutInflater)

        checkPermission()
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Refresh the data or fragment here
                refreshData()
            }
        }

        binding.appBarMain.fab.setOnClickListener {
            val intent = Intent(this@MainActivity,AddTransactionActivity::class.java)
            activityResultLauncher.launch(intent)
        }


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
//                , R.id.nav_settings, R.id.nav_category, R.id.nav_coin, R.id.nav_statistical
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // Access the header view and set OnClickListener for the button
        val headerView = navView.getHeaderView(0)
        val avatar: ImageView = headerView.findViewById(R.id.avatar)
        avatar.setOnClickListener {
            var intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            // Handle menu item selection
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                    drawerLayout.closeDrawer(navView)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.nav_settings)
                    drawerLayout.closeDrawer(navView)
                    true
                }
                R.id.nav_category -> {
                    navController.navigate(R.id.nav_category)
                    drawerLayout.closeDrawer(navView)
                    true
                }
                R.id.nav_coin -> {
                    navController.navigate(R.id.nav_coin)
                    drawerLayout.closeDrawer(navView)
                    true
                }
                R.id.nav_statistical -> {
                    navController.navigate(R.id.nav_statistical)
                    drawerLayout.closeDrawer(navView)
                    true
                }

                R.id.nav_schedule -> {
                    navController.navigate(R.id.nav_schedule)
                    drawerLayout.closeDrawer(navView)
                    true
                }
                else -> false
            }
        }

//        replaceFragment(Home())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshData() {
        // Implement your data refresh logic here
        // For example, if using a fragment:
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is HomeFragment } as? HomeFragment
        if (fragment is HomeFragment) {
            fragment.loadData()
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, BudgetReminderService::class.java).apply {
            putExtra("notificationId", 1)
            putExtra("notificationTitle", "Testing notification")
            putExtra("notificationText", "This is the first notification")
        }
        this.startService(intent)
//        Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
    }

/*    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }*/

    private fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        context.createConfigurationContext(config)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun checkPermission() {
        // Danh sách các quyền cần kiểm tra
        val permissions = arrayOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.VIBRATE,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        val permissionsToRequest = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), 123)
        }
    }

}
