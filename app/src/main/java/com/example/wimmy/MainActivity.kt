package com.example.wimmy

import android.os.Bundle
import android.util.Log
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
import com.example.wimmy.db.TagData
import com.example.wimmy.db.thumbnailData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
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

        InsertDummy()
        /*val go_intent = findViewById(R.id.search) as SearchView
        go_intent.setOnClickListener {
            val intent = Intent(this, com.example.wimmy.SearchView::class.java)
            startActivity(intent)
        }*/


    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle(null)
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

    private fun getDate(year : Int, month : Int, day : Int) : Date {
        val date = Calendar.getInstance()
        date.set(year, month - 1, day)
        return date.time
    }

    private fun InsertDummy() {
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        //더미는 한번만 생성
        if(vm.getSize() != 0) return
        var id1 = vm.Insert(PhotoData(0, "asf", "dump140", "dump", "부산", getDate(2020, 2, 23), false))
        var id2 = vm.Insert(PhotoData(1, "du12mp", "du25mp1", "dump", "창원", getDate(2020, 2, 24), false))
        var id3 = vm.Insert(PhotoData(2, "du4mp", "dum14p1", "dump", "창원", getDate(2020, 2, 25), false))
        var id4 = vm.Insert(PhotoData(3, "du5mp", "dump4362", "dump", "창원", getDate(2020, 2, 25), false))
        var id5 = vm.Insert(PhotoData(4, "dum7p", "dum235p2", "dump", "부산", getDate(2020, 2, 26), false))
        var id6 = vm.Insert(PhotoData(5, "du9m6p", "dump453", "dump", "dump5", getDate(2020, 2, 27), false))
        var id8 = vm.Insert(PhotoData(6, "dum5p", "dump4362", "dump", "dump2", getDate(2020, 2, 28), false))
        var id9 = vm.Insert(PhotoData(7, "du4mp", "dump4", "dump", "부산", getDate(2020, 2, 28), false))
        var id10 = vm.Insert(PhotoData(8, "d3ump", "dump4362", "dump", "du235mp", getDate(2020, 2, 29), false))
        var id11 = vm.Insert(PhotoData(9, "dum9p", "dum4p5", "dump", "du35mp", getDate(2020, 3, 1), false))
        var id13 = vm.Insert(PhotoData(10, "dum75p", "du346mp6", "dump", "dum3p", getDate(2020, 3, 2), false))
        var id14 = vm.Insert(PhotoData(11, "du58mp", "dum42p6", "dump", "dum2p", getDate(2020, 3, 3), false))
        var id15 =  vm.Insert(PhotoData(12, "du8mp", "du42mp6", "dump", "부산", getDate(2020, 3, 3), false))
        var id16 = vm.Insert(PhotoData(13, "dum6p", "dump7124", "dump", "dum4p", getDate(2020, 3, 4), false))
        var id17 = vm.Insert(PhotoData(14, "dum06p", "dum46p7", "dump", "du56p", getDate(2020, 3, 5), false))
        var id18 = vm.Insert(PhotoData(15, "dumrtup", "dump4362", "dump", "du7mp", getDate(2020, 3, 5), false))
        var id19 = vm.Insert(PhotoData(16, "dumyp", "dump53", "dump", "dump", getDate(2020, 4, 5), false))
        var id20 = vm.Insert(PhotoData(17, "dumrp", "dump410", "dump", "부산",getDate(2020, 5, 10), false))
        var id21 = vm.Insert(PhotoData(18, "dump0", "du5mp0", "dump", "d8ump", getDate(2020, 2, 23), false))
        var id22 = vm.Insert(PhotoData(19, "duetmp", "d3ump1", "dump", "창원", getDate(2020, 2, 24), false))
        var id33 = vm.Insert(PhotoData(20, "dumwp", "dump21", "dump", "창원", getDate(2020, 2, 25), false))
        var id44 = vm.Insert(PhotoData(21, "dumdfsp", "4dump2", "dump", "dump", getDate(2020, 2, 25), false))
        var id55 = vm.Insert(PhotoData(22, "dumsdgp", "du3464ump2", "dump", "dump", getDate(2020, 2, 27), false))
        var id77 = vm.Insert(PhotoData(23, "dumhp", "dump3", "dump", "부산", getDate(2020, 2, 27), false))
        var id82 = vm.Insert(PhotoData(24, "dumasp", "dum224p3", "dump", "du23mp", getDate(2020, 2, 28), false))
        var id39 = vm.Insert(PhotoData(25, "dumap", "du24p4", "dump", "dum1p", getDate(2020, 2, 28), false))
        var id140 = vm.Insert(PhotoData(26, "dumfsp", "dump145", "dump", "4dump", getDate(2020, 2, 29), false))
        var id111 = vm.Insert(PhotoData(27, "dumqwp", "dump245", "dump", "창원", getDate(2020, 3, 1), false))
        var id1232 = vm.Insert(PhotoData(28, "dumwp", "dum2p5", "dump", "창원", getDate(2020, 3, 1), false))
        var id1233 = vm.Insert(PhotoData(29, "dumqwep", "du46mp6", "dump", "부산", getDate(2020, 3, 2), false))
        var id1244 = vm.Insert(PhotoData(30, "dumrwp", "dump36", "dump", "dump", getDate(2020, 3, 3), false))
        var id1515 =  vm.Insert(PhotoData(31, "dumtp", "dump366", "dump", "dump", getDate(2020, 3, 3), false))
        var id1656 = vm.Insert(PhotoData(32, "dumyrp", "dum23p7", "dump", "부산", getDate(2020, 3, 4), false))
        var id1577 = vm.Insert(PhotoData(33, "dumyup", "dump7", "dump", "dump", getDate(2020, 3, 5), false))
        var id1378 = vm.Insert(PhotoData(34, "dumiyp", "dum6p7", "dump", "창원", getDate(2020, 3, 5), false))
        var id199 = vm.Insert(PhotoData(35, "dumoip", "dump957", "dump", "창원", getDate(2020, 4, 5), false))
        var id270 = vm.Insert(PhotoData(36, "dumoip", "dump1230", "dump", "dump",getDate(2020, 5, 10), false))

        vm.Insert(TagData(id1, "코로나", "auto"))

        vm.Insert(TagData(id1, "창원", "passive"))
        vm.Insert(TagData(id1, "부산", "auto"))
        vm.Insert(TagData(id1, "문재인", "auto"))
        vm.Insert(TagData(id5, "동물", "auto"))
        vm.Insert(TagData(id5, "곤충", "passive"))
        vm.Insert(TagData(id13, "곤충", "auto"))


        vm.Insert(TagData(id3, "코로나", "auto"))
        vm.Insert(TagData(id3, "상남", "auto"))

        vm.Insert(TagData(id4, "코로나", "auto"))
        vm.Insert(TagData(id4, "상남", "auto"))


        vm.Insert(TagData(id5, "코로나", "auto"))
    }
}


