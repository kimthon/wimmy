package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.wimmy.Main_PhotoView.Companion.photoList
import com.example.wimmy.db.LatLngData
import com.example.wimmy.db.MediaStore_Dao
import com.example.wimmy.db.PhotoData
import com.example.wimmy.db.PhotoViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import kotlinx.android.synthetic.main.main_map.*
import java.text.SimpleDateFormat


class Main_Map: AppCompatActivity(), OnMapReadyCallback {
    private var index = 0
    private lateinit var vm : PhotoViewModel
    private lateinit var mClusterManager: ClusterManager<LatLngData>
    private val builder: LatLngBounds.Builder = LatLngBounds.builder()
    private val ZoomLevel: Int = 12
    private var bounds: LatLngBounds? = null
    private var inserted = false
    private var mLastClickTime: Long = 0

    companion object {
        var latlngdata = arrayListOf<LatLngData>()
    }
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true;
        mMap.uiSettings.isZoomControlsEnabled = true;


        mClusterManager = ClusterManager<LatLngData>(this, mMap)
        getExtra()
        mClusterManager.setRenderer( MarkerClusterRenderer(this, mMap, mClusterManager))

        mMap.setOnCameraChangeListener (mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)

        mMap.setOnInfoWindowClickListener { }
        mMap.setOnMapClickListener { card_view.visibility = View.GONE }

        clusterItemClick(mMap)
        clusterClick(mMap)

        appbar2.setOnClickListener() {
            Log.d("dsfsd","d")
        }
    }

    private fun clusterClick(mMap: GoogleMap) {
        mClusterManager.setOnClusterClickListener { cluster ->
            // 클러스터 클릭 리스너
            val builder_c: LatLngBounds.Builder = LatLngBounds.builder()
            for (item in cluster.items) {
                if (item != null) {
                    builder_c.include(item.position)
                }
            }
            val bounds_c: LatLngBounds = builder_c.build()
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds_c, ZoomLevel))
            val zoom: Float = mMap.cameraPosition.zoom - 0.5f
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
            true
        }
    }

    private fun clusterItemClick(mMap: GoogleMap) {
        mClusterManager.setOnClusterItemClickListener { p0 ->
            // // 클러스터 아이템 클릭 리스너
            card_view.visibility = View.VISIBLE
            val center: CameraUpdate = CameraUpdateFactory.newLatLng(p0?.position)
            mMap!!.animateCamera(center)

            ImageLoder.execute(ImageLoad(map_image, p0.id))
            vm.setName(map_name, p0.id )
            vm.setDate(map_date, p0.id)
            vm.setLocation(map_location, p0.id)
            vm.checkFavorite(map_favorite, p0.id)

            card_view.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(this@Main_Map, PhotoViewPager::class.java)
                    intent.putExtra("index", p0.index)
                    startActivityForResult(intent, 900)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }
            true


            //map_name.setText(photoList[p0!!.index].name)


            /*if(selectedMarker != p0) {
                            if (selectedMarker != null) {
                                tag_marker.setTextColor(Color.BLACK)
                                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone)
                                selectedMarker!!.setIcon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        createDrawableFromView(context, marker_view)
                                    )
                                )
                            }

                            if (marker != null) {
                                tag_marker.setTextColor(Color.WHITE)
                                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue)
                                p0.setIcon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        createDrawableFromView(
                                            context,
                                            marker_view
                                        )
                                    )
                                )
                                selectedMarker = marker
                            }
                        }*/
        }
    }

    private fun createDrawableFromView(context: Context?, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        view.setLayoutParams(
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(
            view.getMeasuredWidth(),
            view.getMeasuredHeight(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun getExtra(){
        inserted = false
        if (intent.hasExtra("location_name")) {
            val getname = intent.getStringExtra("location_name")
            val title: TextView = findViewById(R.id.title_location_name)
            title.text = getname
            vm.setOpenLocationDir(this, getname, this@Main_Map)
        }
    }

    fun addLatLNgData(id : Long, latlng : LatLng) {
        val data = LatLngData(index++, id, latlng)
        if(!inserted) {
            Handler(Looper.getMainLooper()).post { mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(data.latlng, 12F)) }
            inserted = true
        }
        mClusterManager.addItem(data)
        builder.include(data.latlng)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

class MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData>?
) : DefaultClusterRenderer<LatLngData>(context, map, clusterManager) {
    var mLastClickTime: Long = 0
    var selectedMarker: Marker? = null
    private var iconGenerator: IconGenerator? = null
    private var markerImageView: ImageView? = null
    private var context = context
    private var mMap = map

    fun MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData?>?) {

    }

    override fun onBeforeClusterItemRendered(item: LatLngData, markerOptions: MarkerOptions) { // 5
        val marker_view = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
        val tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView
        tag_marker.text = MediaStore_Dao.getNameById(context!!, item.id)

        markerOptions.icon( BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view)) )
    }

    private fun createDrawableFromView(context: Context?, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}