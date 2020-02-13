package com.example.wimmy

import MainFragmentStatePagerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationPresenter
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
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
        val bottomNavigationView = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        val tb: Toolbar = findViewById(R.id.main_toolbar)
        tb.bringToFront()


/*
        val go_intent = findViewById(R.id.activity_main) as View
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
/*
    @SuppressLint("ResourceType")
    private fun configureBottomNavigation(){
        val view1: ViewPager = findViewById(R.id.vp_ac_main_frag_pager)
        val view2: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        view1.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 5)
        view2.setupWithViewPager(view1)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.menu.menu_bottom, null, false)

        view2.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.menu_name) as RelativeLayout
        view2.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.menu_tag) as RelativeLayout
        view2.getTabAt(2)!!.customView = bottomNaviLayout.findViewById(R.id.menu_cal) as RelativeLayout
        view2.getTabAt(3)!!.customView = bottomNaviLayout.findViewById(R.id.menu_location) as RelativeLayout
        view2.getTabAt(4)!!.customView = bottomNaviLayout.findViewById(R.id.menu_map) as RelativeLayout


    }*/

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.menu_name ->{
                val fragmentA = NameFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmentA).commit()
            }
            R.id.menu_tag -> {
                val fragmentB = TagFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmentB).commit()
            }
            R.id.menu_cal -> {
                val fragmentC = CalFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmentC).commit()
            }
            R.id.menu_location -> {
                val fragmentD = LocationFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmentD).commit()
            }
            R.id.menu_map -> {
                val fragmentE = MapFragment()
                supportFragmentManager.beginTransaction().replace(R.id.frame_layout,fragmentE).commit()
            }
        }
        return true
    }





}

