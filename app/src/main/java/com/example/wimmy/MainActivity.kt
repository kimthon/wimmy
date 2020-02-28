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

    private fun InsertDummy() {
        /*
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        //더미는 한번만 생성
        if(vm.getSize() != 0) return
        var id1 = vm.Insert(PhotoData(0, "dump0", "dump1", "dump", "dump", getDate(2020, 2, 23), false))
        var id2 = vm.Insert(PhotoData(0, "du12mp", "dump2", "dump", "dump", getDate(2020, 2, 24), false))
        var id3 = vm.Insert(PhotoData(0, "du4mp", "dump3", "dump", "dump", getDate(2020, 2, 25), false))
        var id4 = vm.Insert(PhotoData(0, "du5mp", "dump4", "dump", "dump", getDate(2020, 2, 25), false))
        var id5 = vm.Insert(PhotoData(0, "dum7p", "dump5", "dump", "dump", getDate(2020, 2, 26), false))
        var id6 = vm.Insert(PhotoData(0, "du9m6p", "dump6", "dump", "dump", getDate(2020, 2, 27), false))
        var id7 = vm.Insert(PhotoData(0, "dum5p", "dump7", "dump", "dump", getDate(2020, 2, 28), false))
        var id8 = vm.Insert(PhotoData(0, "dum5p", "dump8", "dump", "dump", getDate(2020, 2, 28), false))
        var id9 = vm.Insert(PhotoData(0, "du4mp", "dump9", "dump", "dump", getDate(2020, 2, 28), false))
        var id10 = vm.Insert(PhotoData(0, "d3ump", "dump10", "dump", "dump", getDate(2020, 2, 29), false))
        var id11 = vm.Insert(PhotoData(0, "dum9p", "dum4p5", "dump", "dump", getDate(2020, 3, 1), false))
        var id13 = vm.Insert(PhotoData(0, "dum75p", "du346mp6", "dump", "dump", getDate(2020, 3, 2), false))
        var id14 = vm.Insert(PhotoData(0, "du58mp", "dum42p6", "dump", "dump", getDate(2020, 3, 3), false))
        var id15 =  vm.Insert(PhotoData(0, "du8mp", "du42mp6", "dump", "dump", getDate(2020, 3, 3), false))
        var id16 = vm.Insert(PhotoData(0, "dum6p", "dump7124", "dump", "dump", getDate(2020, 3, 4), false))
        var id17 = vm.Insert(PhotoData(0, "dum06p", "dum46p7", "dump", "dump", getDate(2020, 3, 5), false))
        var id18 = vm.Insert(PhotoData(0, "dumrtup", "dum75p7", "dump", "dump", getDate(2020, 3, 5), false))
        var id19 = vm.Insert(PhotoData(0, "dumyp", "dump53", "dump", "dump", getDate(2020, 4, 5), false))
        var id20 = vm.Insert(PhotoData(0, "dumrp", "dump410", "dump", "dump",getDate(2020, 5, 10), false))
        var id21 = vm.Insert(PhotoData(0, "dump0", "du5mp0", "dump", "dump", getDate(2020, 2, 23), false))
        var id22 = vm.Insert(PhotoData(0, "duetmp", "d3ump1", "dump", "dump", getDate(2020, 2, 24), false))
        var id23 = vm.Insert(PhotoData(0, "dumwp", "dump21", "dump", "dump", getDate(2020, 2, 25), false))
        var id24 = vm.Insert(PhotoData(0, "dumdfsp", "4dump2", "dump", "dump", getDate(2020, 2, 25), false))
        var id25 = vm.Insert(PhotoData(0, "dumsdgp", "du3464ump2", "dump", "dump", getDate(2020, 2, 27), false))
        var id26 = vm.Insert(PhotoData(0, "dumhp", "dump3", "dump", "dump", getDate(2020, 2, 27), false))
        var id27 = vm.Insert(PhotoData(0, "dumasp", "dum224p3", "dump", "dump", getDate(2020, 2, 28), false))
        var id28 = vm.Insert(PhotoData(0, "dumap", "du24p4", "dump", "dump", getDate(2020, 2, 28), false))
        var id29 = vm.Insert(PhotoData(0, "dumfsp", "dump145", "dump", "dump", getDate(2020, 2, 29), false))
        var id30 = vm.Insert(PhotoData(0, "dumqwp", "dump245", "dump", "dump", getDate(2020, 3, 1), false))
        var id31 = vm.Insert(PhotoData(0, "dumwp", "dum2p5", "dump", "dump", getDate(2020, 3, 1), false))
        var id32 = vm.Insert(PhotoData(0, "dumqwep", "du46mp6", "dump", "dump", getDate(2020, 3, 2), false))
        var id33 = vm.Insert(PhotoData(0, "dumrwp", "dump36", "dump", "dump", getDate(2020, 3, 3), false))
        var id34 =  vm.Insert(PhotoData(0, "dumtp", "dump366", "dump", "dump", getDate(2020, 3, 3), false))
        var id35 = vm.Insert(PhotoData(0, "dumyrp", "dum23p7", "dump", "dump", getDate(2020, 3, 4), false))
        var id36 = vm.Insert(PhotoData(0, "dumyup", "dump7", "dump", "dump", getDate(2020, 3, 5), false))
        var id37 = vm.Insert(PhotoData(0, "dumiyp", "dum6p7", "dump", "dump", getDate(2020, 3, 5), false))
        var id38 = vm.Insert(PhotoData(0, "dumoip", "dump957", "dump", "dump", getDate(2020, 4, 5), false))
        var id39 = vm.Insert(PhotoData(0, "dumoip", "dump1230", "dump", "dump",getDate(2020, 5, 10), false))
         */
    }
}


