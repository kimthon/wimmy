package com.example.wimmy

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.TagData
import com.example.wimmy.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var toolbar_check: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)

        SetHeader()
        init()

        /*val go_intent = findViewById(R.id.search) as SearchView
        go_intent.setOnClickListener {
            val intent = Intent(this, com.example.wimmy.SearchView::class.java)
            startActivity(intent)
        }*/
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }
     */

    /*
    override fun onContextItemSelected(item: MenuItem): Boolean {
        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bottomNavigationView.setSelectedItemId(R.id.menu_tag)
        return true
    }*/

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var toolbar: Toolbar = findViewById(R.id.main_toolbar)
        if(toolbar_check == false) {
            toolbar_check = true
            toolbar.visibility = View.VISIBLE
        }

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        when(p0.itemId){
            R.id.menu_name ->{
                val fragmentA = NameFragment()
                transaction.replace(R.id.frame_layout,fragmentA, "name")
            }
            R.id.menu_tag -> {
                val fragmentB = TagFragment()
                transaction.replace(R.id.frame_layout,fragmentB, "tag")
            }
            R.id.menu_cal -> {
                val fragmentC = DateFragment()
                transaction.replace(R.id.frame_layout,fragmentC, "cal")
            }
            R.id.menu_location -> {
                val fragmentD = LocationFragment()
                transaction.replace(R.id.frame_layout,fragmentD, "location")
            }
            R.id.menu_map -> {
                val fragmentE = MapFragment()
                transaction.replace(R.id.frame_layout,fragmentE, "map")
            }
        }
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        if(p0.itemId == R.id.menu_map || p0.itemId == R.id.menu_cal ) {
            toolbar_check = false
            toolbar.visibility = View.GONE
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var toolbar: Toolbar = findViewById(R.id.main_toolbar)
        if(toolbar_check == false) {
            toolbar_check = true
            toolbar.visibility = View.VISIBLE
        }
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")
        val tag5: Fragment? = supportFragmentManager.findFragmentByTag("map")

        if(tag1 != null && tag1.isVisible) navigation.menu.findItem(R.id.menu_name).isChecked = true
        if(tag2 != null && tag2.isVisible) navigation.menu.findItem(R.id.menu_tag).isChecked = true
        if(tag3 != null && tag3.isVisible) navigation.menu.findItem(R.id.menu_cal).isChecked = true
        if(tag4 != null && tag4.isVisible) navigation.menu.findItem(R.id.menu_location).isChecked = true
        if(tag5 != null && tag5.isVisible) navigation.menu.findItem(R.id.menu_map).isChecked = true
    }

    fun init(): Boolean{
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragmentA = NameFragment()
        transaction.replace(R.id.frame_layout,fragmentA, "name")
        transaction.commit()
        return true
    }

    private fun getDate(year : Int, month : Int, day : Int) : Date {
        val date = Calendar.getInstance()
        date.set(year, month - 1, day)
        return date.time
    }

}


