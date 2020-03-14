package com.example.wimmy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import com.example.wimmy.Main_Map.Companion.selectedMarker
import com.example.wimmy.MarkerClusterRenderer.Companion.createDrawableFromView
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
import kotlinx.android.synthetic.main.main_map.view.*
import kotlinx.android.synthetic.main.marker_layout.*
import java.text.SimpleDateFormat


class Main_Map: AppCompatActivity(), OnMapReadyCallback {
    private var index = 0
    private lateinit var vm : PhotoViewModel
    private lateinit var mClusterManager: ClusterManager<LatLngData>
    private lateinit var clusterRenderer: DefaultClusterRenderer<LatLngData>
    private val builder: LatLngBounds.Builder = LatLngBounds.builder()
    private val ZoomLevel: Int = 12
    private var size_check: Int = 0
    private var mLastClickTime: Long = 0

    private lateinit var marker_view: View
    private lateinit var tag_marker: TextView


    companion object {
        var selectedMarker: Marker? = null
    }
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapview) as SupportMapFragment
        marker_view = LayoutInflater.from(this).inflate(R.layout.marker_layout, null)
        tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true;
        mMap.uiSettings.isZoomControlsEnabled = true;

        mClusterManager = ClusterManager<LatLngData>(this, mMap)
        clusterRenderer = MarkerClusterRenderer(this, mMap, mClusterManager, marker_view)
        getExtra()
        mClusterManager.setRenderer(clusterRenderer)




        mMap.setOnCameraChangeListener (mClusterManager)
        mMap.setOnMarkerClickListener(mClusterManager)

        mMap.setOnInfoWindowClickListener { }
        mMap.setOnMapClickListener {
            card_view.visibility = View.GONE
            if(selectedMarker != null) {
                selectedMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_view)))
                selectedMarker = null
            }
        }

        clusterItemClick(mMap)
        clusterClick(mMap)

        refresh_loaction.setOnClickListener() {
            boundmap()
        }
    }

    fun cameraInit() {
        if(size_check < 100) {
            boundmap()
        }
        loading_location_name.visibility = View.GONE
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

            Log.d("qweqwe","wqe")
            changeRenderer(p0)

            card_view.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val intent = Intent(this@Main_Map, PhotoViewPager::class.java)
                    intent.putExtra("index", p0.index)
                    startActivityForResult(intent, 900)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }



            true
        }
    }

    fun boundmap() {
        val bounds: LatLngBounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, ZoomLevel))
        val zoom: Float = mMap.getCameraPosition().zoom - 0.5f
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
    }

    private fun changeRenderer(item: LatLngData) {
        if(selectedMarker != clusterRenderer.getMarker(item)) {
            if (selectedMarker != null) {
                tag_marker.setTextColor(Color.BLACK)
                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone)
                selectedMarker!!.setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        createDrawableFromView(this, marker_view)
                    )
                )
            }

            if (clusterRenderer.getMarker(item) != null) {
                tag_marker.setTextColor(Color.WHITE)
                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone_blue)
                tag_marker.text = MediaStore_Dao.getNameById(this, item.id)
                clusterRenderer.getMarker(item).setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        createDrawableFromView(
                            this,
                            marker_view
                        )
                    )
                )
                selectedMarker = clusterRenderer.getMarker(item)
                tag_marker.setTextColor(Color.BLACK)
                tag_marker.setBackgroundResource(R.drawable.ic_marker_phone)
            }
        }
    }
    fun getExtra(){
        size_check = 0
        if (intent.hasExtra("location_name")) {
            val getname = intent.getStringExtra("location_name")
            val title: TextView = findViewById(R.id.title_location_name)
            title.text = getname
            vm.setOpenLocationDir(this, getname, this@Main_Map)
        }
    }

    fun addLatLNgData(id : Long, latlng : LatLng) {
        val data = LatLngData(index++, id, latlng)
        if(size_check == 0) {
            Handler(Looper.getMainLooper()).post { mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(data.latlng, 12F)) }
        }
        mClusterManager.addItem(data)
        builder.include(data.latlng)
        size_check++
        if(size_check == 100) {
            Handler(Looper.getMainLooper()).post {boundmap()}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}

class MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData>?,
                            marker_view: View) : DefaultClusterRenderer<LatLngData>(context, map, clusterManager) {
    private val context = context
    private var marker_view = marker_view
    private lateinit var tag_marker: TextView

    override fun onBeforeClusterItemRendered(item: LatLngData, markerOptions: MarkerOptions) { // 5
        tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView
        tag_marker.text = MediaStore_Dao.getNameById(context!!, item.id)
        markerOptions.icon( BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view)) )
    }

    override fun shouldRenderAsCluster(cluster: Cluster<LatLngData>?): Boolean {
        super.shouldRenderAsCluster(cluster)
        return cluster != null && cluster.size >= 3
    }

    override fun onClustersChanged(clusters: MutableSet<out Cluster<LatLngData>>?) {
        super.onClustersChanged(clusters)

        if(selectedMarker != null) {
            selectedMarker!!.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view)))
            selectedMarker = null
        }
    }

    companion object {
        fun createDrawableFromView(context: Context?, view: View): Bitmap {
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

}