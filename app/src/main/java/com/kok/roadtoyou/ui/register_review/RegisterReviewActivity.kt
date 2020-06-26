package com.kok.roadtoyou.ui.register_review

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.kok.roadtoyou.BackPressCloseHandler
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

    private lateinit var backPressCloseHandler: BackPressCloseHandler

    lateinit var plansDB: DatabaseReference
    lateinit var reviewDB: DatabaseReference
    lateinit var userDB: DatabaseReference
    lateinit var storage: FirebaseStorage

    lateinit var adapter: RegisterViewPagerAdapter
    lateinit var planItem: PlanItem
    var reviewList = mutableListOf<ReviewItem>()

    // 카메라 & 갤러리
    lateinit var imagePicker: ImagePicker
    private var index = -1
    private var imageFilePath =""

    private val REQUEST_IMAGE_CAPTURE = 1001
    private val REQ_CAMERA = 3003
    private val REQ_READ_GALLERY = 4004
    private val REQ_WRITE_GALLERY = 5005

    private var cameraPermissionFlag = false
    private var readStoragePermissionFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_review)
        init()
    }

    private fun init() {
        initToolbar()
        initData()

        //ImagePicker 초기 설정
        imagePicker = ImagePicker.create(this)
            .toolbarImageTitle("사진 선택")
            .toolbarDoneButtonText("완료")
            .limit(10)
            .showCamera(false)
            .imageDirectory("RoadToYou")
            .theme(R.style.ImagePickerTheme)
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
                planItem = DataConverter().dataConvertPlanItem(p0.value.toString())
                Log.d("Log_Plan_Item_RegisterReview",planItem.toString())
                if (planItem.placeList != null) {
                    for (i in planItem.placeList!!.indices) {
                        val item = planItem.placeList!![i]
                        reviewList.add(ReviewItem(1, item.title, item.id,null, null, null))
                    }
                }
                initView()
            }
        })
    }

    private fun initView() {
        adapter = RegisterViewPagerAdapter(reviewList)
        adapter.itemClickListener = object : RegisterViewPagerAdapter.OnItemClickListener {
            override fun addPhotoOnClick(view: View, position: Int) {
                cameraPermission()
                if (cameraPermissionFlag) {
                    index = position
                    dispatchTakePictureIntent()
                }
            }

            override fun addGalleryOnClick(view: View, position: Int) {
                writeStoragePermission()
                readStoragePermission()
                if (readStoragePermissionFlag) {
                    index = position
                    imagePicker.start()
                }
            }
        }
        viewpager_register_review.adapter = adapter

        TabLayoutMediator(tabLayout_register_review, viewpager_register_review) { _, _ -> }.attach()

        //TabLayout Mode 변경
        if (tabLayout_register_review.tabCount > 6)
            tabLayout_register_review.tabMode = TabLayout.MODE_SCROLLABLE
    }

    private fun uploadReview(firstImgIndex: Int) {

        val editText = EditText(this)
        val builder = AlertDialog.Builder(this)
            .setTitle("리뷰 제목을 입력해 주세요")
            .setView(editText)
            .setPositiveButton("확인") { _, _ ->
                val title = editText.text.toString()
                if (title.length < 2) {
                    Toast.makeText(this, "제목은 두 글자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        userDB = FirebaseDatabase.getInstance().getReference("users/${user.uid}")
                        val key = userDB.child("reviewList").push().key!!
                        userDB.child("reviewList/${key}").setValue(title)

                        //Cover Page 추가
                        reviewList.add(0, ReviewItem(0, null, null, null, null, null))
                        val reviewInfo = ReviewInfo(title, planItem.period!!, key, planItem.planID!!,
                            reviewList[firstImgIndex].imgList!![0], user.uid, 0, reviewList)
                        reviewDB = FirebaseDatabase.getInstance().getReference("reviews")
                        reviewDB.child("/$key").setValue(reviewInfo)
                        finish()
                    } else {
                        Log.e("NOT USER", "NOT CURRENT USER")
                    }
                }
            }
            .setNegativeButton("취소") { _, _ -> }
        builder.create().show()
    }



    ////////////////////////////////// Camera & Gallery 접근 ////////////////////////////////////////

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) { null }

                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, packageName, it)
                    Log.d("Log_Photo_Uri", photoURI.toString())
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "RoadToYou_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            imageFilePath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            reviewList[index].imgList!!.add(imageFilePath)                                  // Add Image URI
            Log.d("Log_Image_File_Path_Camera", imageFilePath)
            imageFilePath = ""
            adapter.notifyDataSetChanged()
        }
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            val images =  ImagePicker.getImages(data) as ArrayList<Image>
            for (i in images.indices) {
                imageFilePath = images[i].path
                reviewList[index].imgList!!.add(imageFilePath)
                Log.d("Log_Image_File_Path_ImagePicker", imageFilePath)
//                storage = FirebaseStorage.getInstance()
//                val storageRef = storage.getReference("images")
//                val file = Uri.fromFile(File(imageFilePath))
//                //val riversRef = storageRef.child("images/${file.lastPathSegment}")
//                val riversRef = storageRef.child("images/$imageFilePath")
//                val uploadTask = riversRef.putFile(file)
//
//                // Register observers to listen for when the download is done or if it fails
//                uploadTask.addOnFailureListener {
//                    // Handle unsuccessful uploads
//                }.addOnSuccessListener {
//                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                    // ...
//                    adapter.notifyDataSetChanged()
//                }
            }
            adapter.notifyDataSetChanged()
        } else {
            Log.d("Log_Image_File_Path_ImagePicker", "imagepicker data null")
        }

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


    /////////////////////////////////// Toolbar & BackPress ////////////////////////////////////////
    private fun initToolbar() {
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        backPressCloseHandler = BackPressCloseHandler(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.register_review_menu, menu)
        return  true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                backPressCloseHandler.onBackPressed_save()
                return true
            }
            R.id.action_save -> {
                var firstImgIndex = -1
                for (i in reviewList.indices) {
                    //리뷰 글이 있을 때,
                    if (!reviewList[i].review.isNullOrEmpty() && !reviewList[i].review.equals("null")) {
                        reviewList[i].review += "^^&**!@"
                        if (reviewList[i].imgList != null && reviewList[i].imgList!!.isNotEmpty()) {
                            firstImgIndex = i
                        } else {
                            Toast.makeText(this, "리뷰를 작성한 장소는 사진이 한 장 이상 꼭 있어야 합니다.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    } else {  //리뷰 글이 없을 때,
                        if (reviewList[i].imgList != null && reviewList[i].imgList!!.isNotEmpty()) {
                            Toast.makeText(this, "사진을 첨부한 장소는 리뷰가 꼭 있어야 합니다.", Toast.LENGTH_SHORT).show()
                            return false
                        } else {    //리뷰 글도 없고 사진도 없을 때,
                            Toast.makeText(this, "리뷰를 모두 작성해주세요.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    }
                }
                if (firstImgIndex < 0) {
                    Toast.makeText(this, "장소마다 사진은 한 장 이상 꼭 있어야 합니다.", Toast.LENGTH_SHORT).show()
                } else {
                    uploadReview(firstImgIndex)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        backPressCloseHandler.onBackPressed_save()
    }

}