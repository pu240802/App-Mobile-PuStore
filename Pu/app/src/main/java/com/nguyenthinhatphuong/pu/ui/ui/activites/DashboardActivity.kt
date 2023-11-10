package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.databinding.ActivityDashboardBinding
import com.nguyenthinhatphuong.pu.utils.Constants

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(this@DashboardActivity, R.drawable.app_gradient_color_background)
        )

        val isAdmin = intent.getBooleanExtra(Constants.EXTRA_IS_ADMIN, false)

        val menuIds = if (isAdmin) {
            setOf(
                R.id.navigation_products, R.id.navigation_dashboard, R.id.navigation_orders,
                R.id.navigation_sold_products, R.id.navigation_shop_orders
            )
        } else {
            setOf(
                R.id.navigation_products, R.id.navigation_dashboard, R.id.navigation_orders
            )
        }

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_dashboard)
        val appBarConfiguration = AppBarConfiguration(menuIds)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.menu.findItem(R.id.navigation_sold_products)?.isVisible = isAdmin
        navView.menu.findItem(R.id.navigation_shop_orders)?.isVisible = isAdmin
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }
}