package com.kok.roadtoyou.ui.review

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.*
import com.kok.roadtoyou.DataConverter
import com.kok.roadtoyou.R
import com.kok.roadtoyou.ui.addplan.PlanItem
import kotlinx.android.synthetic.main.activity_register_review.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterReviewActivity : AppCompatActivity() {

    lateinit var plansDB: DatabaseReference

    lateinit var adapter: RegisterViewPagerAdapter
    var itemList = ArrayList<ReviewItem>()
    private var imgArray = ArrayList<ArrayList<String>>()

    // 카메라
    private var index = -1
    private var imageFilePath =""

    private val REQUEST_IMAGE_CAPTURE = 1111
    private val REQUEST_IMAGE_STORAGE = 1112
    private val REQ_CAMERA = 1000
    private val REQ_READ_GALLERY = 1001
    private val REQ_WRITE_GALLERY = 1002

    private var cameraPermissionFlag = false
    private var readStoragePermissionFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_review)
        init()
    }

    private fun init() {
        initData()
    }

    private fun initData() {
        val planID = intent.getStringExtra("PLAN_ID")
        plansDB = FirebaseDatabase.getInstance().getReference("plans")
        plansDB.orderByKey().equalTo(planID).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d("Log_Plan_Item_RegisterReview_before", p0.value.toString())
                val planItem = DataConverter().convertPlanItem(p0.value.toString())
                Log.d("Log_Plan_Item_RegisterReview",planItem.toString())
                if (planItem.placeList != null) {
                    for (i in planItem.placeList!!.indices) {
                        val item = planItem.placeList!![i]
                        itemList.add(ReviewItem(item.title, null, null, null))
                    }
                }
                initView()
            }
        })
    }

    private fun initView() {
        adapter = RegisterViewPagerAdapter(itemList)
        adapter.itemClickListener = object : RegisterViewPagerAdapter.OnItemClickListener {
            override fun addPhotoOnClick(view: View, position: Int) {
                cameraPermission()
                if (cameraPermissionFlag)
                    camera(position)
            }

            override fun addGalleryOnClick(view: View, position: Int) {
                writeStoragePermission()
                readStoragePermission()
                if (readStoragePermissionFlag)
                    storage(position)
            }

            override fun hash(tag: String, position: Int) {
                TODO("Not yet implemented")
            }
        }
        viewpager_register_review.adapter = adapter

        TabLayoutMediator(tabLayout_register_review, viewpager_register_review) { _, _ -> }.attach()

        //TabLayout Mode 변경
        if (tabLayout_register_review.tabCount > 6)
            tabLayout_register_review.tabMode = TabLayout.MODE_SCROLLABLE
    }



    ////////////////////////////////// Camera & Gallery 접근 ////////////////////////////////////////

    private fun camera(position: Int) {
        index = position
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, packageName, photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File {
        var imageFileName = ""
        val timeStamp = SimpleDateFormat("yyMMddHHmm").format(Date())
        imageFileName = "TEST_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        imageFilePath = image.absolutePath
        imageFileName += ".jpg"
        return image
    }

    private fun storage(position: Int) {
        index = position
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(Intent.createChooser(intent, "사진 선택하기"), REQUEST_IMAGE_STORAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                val file = File(imageFilePath)
                imgArray[viewpager_register_review.currentItem].add(imageFilePath)      // Add Image URI
                itemList[index].imgList?.add(imageFilePath)
            }
        } else if (requestCode == REQUEST_IMAGE_STORAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data?.clipData != null) {
                    val clipData = data.clipData!!
                    if (clipData.itemCount < 10) {
                        for(i in 0 until clipData.itemCount) {
                            val inputStream = contentResolver.openInputStream(clipData.getItemAt(i).uri)
                            val imgName = absolutelyPath(clipData.getItemAt(i).uri!!)       // Add Image URI
                            imgArray[viewpager_register_review.currentItem].add(imgName)
                            itemList[index].imgList?.add(imgName)
                        }
                    } else {
                        Toast.makeText(this, "사진은 10개까지 선택가능합니다", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun absolutelyPath(path: Uri): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor = contentResolver.query(path, proj, null, null, null)!!
        val index = c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c.moveToFirst()
        return c.getString(index)
    }




    ////////////////////////////////////// Permission Check ////////////////////////////////////////

    private fun cameraPermission() {
        val cameraPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQ_CAMERA)
        } else {
            cameraPermissionFlag = true
        }
    }

    private fun writeStoragePermission() {
        val writeStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_WRITE_GALLERY)
        } else {
            readStoragePermissionFlag = true    // Permission ok
        }
    }

    private fun readStoragePermission() {
        val readStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQ_READ_GALLERY)
        } else {
            readStoragePermissionFlag = true    // Permission ok
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQ_CAMERA-> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    cameraPermissionFlag = true    // Permission ok
                return
            }
            REQ_READ_GALLERY-> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    readStoragePermissionFlag = true    // Permission ok
                return
            }
            REQ_WRITE_GALLERY-> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    readStoragePermissionFlag = true    // Permission ok
                return
            }
        }
    }


}