package com.example.wimmy

import MainFragmentStatePagerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*

import android.widget.Button
import android.widget.TabHost
import android.widget.TabWidget
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("CAST_NEVER_SUCCEEDS")
class MainActivity : AppCompatActivity() {
    /*var photoList = arrayListOf<PhotoData>(
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false),
        PhotoData("dummy", "dummy", "dummy", false)
    )
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureBottomNavigation()
/*
        val go_intent = findViewById(R.id.menu_map) as View
        go_intent.setOnClickListener {
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
        }
        SetView()
        SetHeader()*/
    }
/*
    fun SetView() {

        val recyclerView = findViewById<RecyclerView>(R.id.mRecycleView)
        val mainAdapter = MainAdapter(this, photoList)
        recyclerView.adapter = mainAdapter
        val lm = GridLayoutManager(this, 3)
        recyclerView.layoutManager = lm
    }

    fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
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
*/

    private fun configureBottomNavigation(){
        val view1: ViewPager = findViewById(R.id.vp_ac_main_frag_pager)
        val view2: TabLayout = findViewById(R.id.tl_ac_main_bottom_menu)
        view1.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 5)
        view2.setupWithViewPager(view1)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        view2.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.menu_name) as RelativeLayout
        view2.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.menu_tag) as RelativeLayout
        view2.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.menu_cal) as RelativeLayout
        view2.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.menu_location) as RelativeLayout
        view2.getTabAt(4)!!.customView = bottomNaviLayout.findViewById(R.id.menu_map) as RelativeLayout


    }

}

