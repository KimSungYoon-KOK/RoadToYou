package com.kok.roadtoyou.ui.review

import android.content.Context
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReviewBottomSheetDialog(
    val c: Context,
    val posX:Double,
    val posY:Double,
    val location:String,
    val call:String,
    var like:Int,
    var dislike:Int
): BottomSheetDialogFragment(), OnMapReadyCallback {

    override fun onMapReady(p0: GoogleMap?) {
        val LatLng = LatLng(posX, posY)
        val marker = MarkerOptions()
        marker.position(LatLng)
        marker.icon(BitmapDescriptorFactory.fromResource(com.inseoul.R.drawable.default_marker))
        p0!!.addMarker(marker)
        p0!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng, 15f))
    }

}