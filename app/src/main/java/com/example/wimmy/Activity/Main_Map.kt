package com.example.wimmy.Activity

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wimmy.*
import com.example.wimmy.Activity.Main_Map.Companion.selectedMarker
import com.example.wimmy.Activity.Main_PhotoView.Companion.list
import com.example.wimmy.Activity.MarkerClusterRenderer.Companion.createDrawableFromView
import com.example.wimmy.R
import com.example.wimmy.db.LatLngData
import com.example.wimmy.db.PhotoViewModel
import com.example.wimmy.db.thumbnailData
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.main_map.*


class Main_Map: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var vm : PhotoViewModel
    private lateinit var mClusterManager: ClusterManager<LatLngData>
    private lateinit var clusterRenderer: DefaultClusterRenderer<LatLngData>
    private val builder: LatLngBounds.Builder = LatLngBounds.builder()
    private val zoomLevel: Int = 12
    private var mLastClickTime: Long = 0
    private var selected_id : Long = 0

    private lateinit var marker_view: View
    private lateinit var tag_marker: TextView
    private lateinit var tag_marker_layout: ViewGroup.LayoutParams

    private val latLngList = ArrayList<LatLngData>()


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
        tag_marker_layout = tag_marker.layoutParams

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        mClusterManager = ClusterManager<LatLngData>(this, mMap)
        mClusterManager.setAlgorithm(GridBasedAlgorithm())
        clusterRenderer = MarkerClusterRenderer(this, mMap, mClusterManager, marker_view, vm)
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

        refresh_loaction.setOnClickListener {
            boundmap()
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds_c, zoomLevel))
            val zoom: Float = mMap.cameraPosition.zoom - 0.5f
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
            true
        }
    }

    private fun clusterItemClick(mMap: GoogleMap) {
        mClusterManager.setOnClusterItemClickListener { p0 ->
            // // 클러스터 아이템 클릭 리스너
            card_view.visibility = View.VISIBLE
            selected_id = p0.id
            val center: CameraUpdate = CameraUpdateFactory.newLatLng(p0?.position)
            mMap.animateCamera(center)
            ImageLoder.execute(ImageLoad(this, map_image, p0.id, 1))
            DBThread.execute {
                val data = vm.getName(applicationContext, p0.id)
                MainHandler.post { map_name.text = data }
            }

            DBThread.execute {
                val data = vm.getStringDate(applicationContext, p0.id)
                MainHandler.post { map_date.text = data}
            }

            DBThread.execute {
                val data = vm.getLocation(applicationContext, p0.id)
                MainHandler.post { map_location.text = data}
            }

            DBThread.execute {
                val data = vm.getTags(p0.id)
                MainHandler.post {
                    map_tag.text = data
                    clusterRenderer.getMarker(p0).title = data
                    clusterRenderer.getMarker(p0).showInfoWindow()
                }
            }

            DBThread.execute {
                val data = vm.getFavorite(p0.id)
                MainHandler.post {
                    if (data) map_favorite.setImageResource(R.drawable.ic_favorite_checked)
                    else map_favorite.setImageResource(R.drawable.ic_favorite)
                }
            }

            changeRenderer(p0)

            card_view.setOnClickListener {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    val index = list.indexOfFirst { it.photo_id == p0.id }
                    val intent = Intent(this@Main_Map, PhotoViewPager::class.java)
                    intent.putExtra("index", index)
                    startActivityForResult(intent, 900)
                }
                mLastClickTime = SystemClock.elapsedRealtime()
            }

            true
        }
    }

    fun boundmap() {
        val bounds: LatLngBounds = builder.build()
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, zoomLevel))
        val zoom: Float = mMap.cameraPosition.zoom - 0.5f
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom))
    }

    private fun changeRenderer(item: LatLngData) {
        if(selectedMarker != clusterRenderer.getMarker(item)) {
            if (selectedMarker != null) {
                markerScale(100)
                tag_marker.setBackgroundResource(R.drawable.map_marker_free)
                selectedMarker!!.setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        createDrawableFromView(this, marker_view)
                    )
                )
            }

            if (clusterRenderer.getMarker(item) != null) {
                tag_marker.setBackgroundResource(R.drawable.map_marker_checked)

                markerScale(150)
                clusterRenderer.getMarker(item).setIcon(
                    BitmapDescriptorFactory.fromBitmap(
                        createDrawableFromView(
                            this,
                            marker_view
                        )
                    )
                )
                clusterRenderer.getMarker(item).showInfoWindow()
                selectedMarker = clusterRenderer.getMarker(item)
                markerScale(100)
                tag_marker.setTextColor(Color.BLACK)
                tag_marker.setBackgroundResource(R.drawable.map_marker_free)
            }
        }
    }

    fun markerScale(size: Int) {
        tag_marker_layout.width = size
        tag_marker_layout.height = size
        tag_marker_layout.layoutAnimationParameters
    }

    fun getExtra(){
        if (intent.hasExtra("location_name")) {
            list.clear()
            val getname = intent.getStringExtra("location_name")
            val title: TextView = findViewById(R.id.title_location_name)
            title.text = getname

            val liveData = vm.getOpenLocationDirIdList(getname!!)
            liveData.observe(this, Observer { idList ->
                loading_location_name.visibility = View.VISIBLE
                DirectoryThread.queue.clear()
                DirectoryThread.execute {
                    var i = 0
                    for (id in idList) {
                        do {
                            val pre = if (i < latLngList.size) {
                                (latLngList[i].id - id).toInt()
                            } else {
                                Int.MAX_VALUE
                            }

                            // 오름차 순 정렬
                            // pre < 0 : 이전 데이터가 사라진 경우
                            if (pre < 0) {
                                mClusterManager.removeItem(latLngList[i])
                                if(selected_id == latLngList[i].id) MainHandler.post { card_view.visibility = View.GONE }
                                latLngList.removeAt(i)
                                MainHandler.post{ mClusterManager.cluster() }
                                continue
                            }
                            //그대로 일 경우
                            else if (pre == 0) {
                                ++i
                                break
                            }
                            //삽입
                            else {
                                val latLng = vm.getLatLngById(this.applicationContext, id)
                                if (latLng != null) {
                                    val name = vm.getName(this.applicationContext, id)
                                    list.add(thumbnailData(id, name))
                                    addLatLNgData(id, latLng)
                                    MainHandler.post{ mClusterManager.cluster() }
                                }
                                ++i
                                break
                            }
                        } while (true)
                    }
                    MainHandler.post {
                        loading_location_name.visibility = View.GONE
                    }
                }
            })
        }
    }

    fun addLatLNgData(id : Long, latlng : LatLng) {
        val data = LatLngData(id, latlng)
        if(latLngList.size == 0) {
            Handler(Looper.getMainLooper()).post {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(data.latlng, 12F))
            }
        }
        mClusterManager.addItem(data)
        latLngList.add(data)
        builder.include(data.latlng)

        if(latLngList.size == 100) {
            Handler(Looper.getMainLooper()).post {boundmap()}
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}

class MarkerClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<LatLngData>?,
                            marker_view: View, vm: PhotoViewModel) : DefaultClusterRenderer<LatLngData>(context, map, clusterManager) {
    private val context = context
    private var marker_view = marker_view
    private lateinit var tag_marker: TextView
    private var vm = vm


    override fun onBeforeClusterItemRendered(item: LatLngData, markerOptions: MarkerOptions) { // 5
        tag_marker = marker_view.findViewById(R.id.tag_marker) as TextView
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(context, marker_view)))
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