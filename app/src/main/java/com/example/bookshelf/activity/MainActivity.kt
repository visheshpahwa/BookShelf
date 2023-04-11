package com.example.bookshelf.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bookshelf.fragment.ProfileFragment
import com.example.bookshelf.R
import com.example.bookshelf.fragment.AboutFragment
import com.example.bookshelf.fragment.DashboardFragment
import com.example.bookshelf.fragment.FavouritesFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var coordinatorLayout: CoordinatorLayout

    private var previousMenuItem:MenuItem?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openDashboard()


        drawerLayout=findViewById(R.id.drawerLayout)
        frameLayout=findViewById(R.id.frameLayout)
        navigationView=findViewById(R.id.navigationView)
        coordinatorLayout=findViewById(R.id.coordinateLayout)
        toolbar=findViewById(R.id.toolbar)
        navigationView.setCheckedItem(R.id.dashboard)
        setUpToolbar()
        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem!=null){
                previousMenuItem?.isChecked=false

            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
            when(it.itemId){
                R.id.dashboard ->{
                    openDashboard()
                    navigationView.setCheckedItem(R.id.dashboard)
                    drawerLayout.closeDrawers()
                }
//                R.id.profile ->{
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.frameLayout, ProfileFragment())
//                        .addToBackStack("Profile")
//                        .commit()
//                    supportActionBar?.title="Profile"
//                    drawerLayout.closeDrawers()
//                }
                R.id.about ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, AboutFragment())
                        .addToBackStack("About")
                        .commit()
                    supportActionBar?.title="About"
                    drawerLayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavouritesFragment())
                        .addToBackStack("Favourites")
                        .commit()
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }
    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDashboard(){

        val fragment= DashboardFragment()
        val transaction=supportFragmentManager.beginTransaction()

            transaction.replace(R.id.frameLayout,fragment)
            transaction.commit()
        supportActionBar?.title="Dashboard"

    }

    override fun onBackPressed() {

        when(supportFragmentManager.findFragmentById(R.id.frameLayout)){
            !is DashboardFragment -> openDashboard()

            else-> super.onBackPressed()
        }
    }

}