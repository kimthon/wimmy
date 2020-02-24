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
import com.example.wimmy.fragment.*
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    var toolbar_check: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        var toolbar: Toolbar = findViewById(R.id.main_toolbar)
        bnv.setOnNavigationItemSelectedListener(this)

        SetHeader(toolbar)
        init()

        /*val go_intent = findViewById(R.id.search) as SearchView
        go_intent.setOnClickListener {
            val intent = Intent(this, com.example.wimmy.SearchView::class.java)
            startActivity(intent)
        }*/

        var vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump1", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump2", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump3", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump4", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump5", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump6", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump7", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump8", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump9", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump12", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump23", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump45", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump76", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump69", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "23", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "df", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "sdf", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "ddfs", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "jght", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "jyt", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "yjp6", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dumyjp6", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dumpte7", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dumpwet7", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump7wet", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "d234ump8", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump359", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump1265", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump23765", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump47895", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump7635", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dump69636", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "237657", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "d52f", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "sd654f", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "ddf124s", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "jghwetbt", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "jyetmyt", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "yjp6tyt", "dump", "dump", 0, false))
        vm.Insert(PhotoData(0, "dump", "dumyjpty6", "dump", "dump", 0, false))
    }

    private fun SetHeader(toolbar: Toolbar) {
        toolbar.bringToFront()
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            //R.id.favorate_menu =>
            //
        }
        return super .onOptionsItemSelected(item)
    }


    /*override fun onContextItemSelected(item: MenuItem): Boolean {
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

    fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")
        val tag5: Fragment? = supportFragmentManager.findFragmentByTag("map")

        if(tag1 != null && tag1.isVisible()) navigation.getMenu().findItem(R.id.menu_name).setChecked(true)
        if(tag2 != null && tag2.isVisible()) navigation.getMenu().findItem(R.id.menu_tag).setChecked(true)
        if(tag3 != null && tag3.isVisible()) navigation.getMenu().findItem(R.id.menu_cal).setChecked(true)
        if(tag4 != null && tag4.isVisible()) navigation.getMenu().findItem(R.id.menu_location).setChecked(true)
        if(tag5 != null && tag5.isVisible()) navigation.getMenu().findItem(R.id.menu_map).setChecked(true)


    }

    fun init(): Boolean{
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        val fragmentA = NameFragment()
        transaction.replace(R.id.frame_layout,fragmentA, "name")
        transaction.commit()
        return true
    }
}


