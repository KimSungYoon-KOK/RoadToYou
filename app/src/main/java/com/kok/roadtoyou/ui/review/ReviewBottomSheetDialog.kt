package com.kok.roadtoyou.ui.review

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kok.roadtoyou.R
import kotlinx.android.synthetic.main.dialog_review_viewpager_bottom_sheet.view.*

class ReviewBottomSheetDialog(
    private val lat:Double,
    private val lng:Double,
    private val location:String
): BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var mView: View
    private lateinit var mapFragment: SupportMapFragment

    override fun onMapReady(p0: GoogleMap?) {
        val latlng = LatLng(lat, lng)
        val marker = MarkerOptions()
        marker.position(latlng)
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker))
        p0?.addMarker(marker)
        p0?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mView = inflater.inflate(R.layout.dialog_review_viewpager_bottom_sheet, container, false)
        mView.location.text = location
        mView.bottomSheet_btn.setOnClickListener {
            dismiss()
        }

        mapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.bottom_sheet_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return mView
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        activity!!.supportFragmentManager.beginTransaction().remove(mapFragment).commit()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        activity!!.supportFragmentManager.beginTransaction().remove(mapFragment).commit()

    }

    fun getInstance(): BottomSheetDialogFragment {
        return BottomSheetDialogFragment()
    }

}