package com.pratclot.dogs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.MenuItemCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pratclot.dogs.data.MainViewModel
import com.pratclot.dogs.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navC: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        viewModel.title
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { supportActionBar?.setTitle(it) },
                onError = {},
                onComplete = {}
            )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_content) as NavHostFragment
        navC = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navC)
        appBarConfiguration = AppBarConfiguration(navC.graph)

        findViewById<BottomNavigationView>(R.id.nav_view).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_list -> {
                    navC.navigate(
                        NavDeepLinkRequest.Builder.fromUri("com.pratclot.dogs://list".toUri())
                            .build()
                    )
                    true
                }

                R.id.menu_item_favourites -> {
                    navC.navigate(
                        NavDeepLinkRequest.Builder.fromUri("com.pratclot.dogs://favourites".toUri())
                            .build()
                    )
                    true
                }

                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navC, appBarConfiguration)
    }
}