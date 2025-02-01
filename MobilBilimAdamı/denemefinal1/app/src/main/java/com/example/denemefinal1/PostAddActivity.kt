package com.example.denemefinal1

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class PostAddActivity : AppCompatActivity() {

    // UI elemanları
    private lateinit var imageView: ImageView
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var birthPlaceInput: EditText
    private lateinit var birthDateInput: EditText
    private lateinit var deathDateInput: EditText
    private lateinit var contributionInput: EditText
    private lateinit var saveButton: Button
    private lateinit var selectImageButton: Button

    // Firebase Firestore ve Storage nesneleri
    private var selectedImageUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance() // Kullanıcı oturum bilgisi için

    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION = 1001
        private const val PICK_IMAGE_REQUEST = 1000
        private const val GALLERY_PERMISSION_REQUEST_CODE = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_add)

        // UI elemanlarını bağlama
        imageView = findViewById(R.id.imageView)
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        birthPlaceInput = findViewById(R.id.birthPlaceInput)
        birthDateInput = findViewById(R.id.birthDateInput)
        deathDateInput = findViewById(R.id.deathDateInput)
        contributionInput = findViewById(R.id.contributionInput)
        saveButton = findViewById(R.id.saveButton)
        selectImageButton = findViewById(R.id.selectImageButton)

        // Resim seçme butonu
        selectImageButton.setOnClickListener {
            checkGalleryPermission() // Galeriye erişim izni kontrolü
        }

        // Kaydet butonu
        saveButton.setOnClickListener {
            savePost() // Gönderiyi kaydet
        }
    }

    /**
     * Galeri erişim izni kontrolü yapar ve gerekirse izin ister.
     */
    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 ve üzeri için izin kontrolü
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery() // İzin varsa galeriyi aç
            }
        } else {
            // Android 13 altı için izin kontrolü
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    GALLERY_PERMISSION_REQUEST_CODE
                )
            } else {
                openGallery() // İzin varsa galeriyi aç
            }
        }
    }

    /**
     * İzin sonuçlarını değerlendirir.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery() // İzin verilmişse galeriyi aç
            } else {
                Toast.makeText(this, "Galeriyi açmak için izin gerekli!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Galeriyi açmak için bir intent başlatır.
     */
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Sadece resim seçimi
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    /**
     * Galeriden seçilen resmi alır ve gösterir.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            imageView.setImageURI(selectedImageUri) // Resmi ImageView'de göster
        }
    }

    /**
     * Gönderiyi kaydeder ve Firestore'a ekler.
     */
    private fun savePost() {
        val name = nameInput.text.toString()
        val surname = surnameInput.text.toString()
        val birthPlace = birthPlaceInput.text.toString()
        val birthDate = birthDateInput.text.toString()
        val deathDate = deathDateInput.text.toString()
        val contribution = contributionInput.text.toString()

        // Giriş yapan kullanıcının e-posta adresini al
        val userEmail = auth.currentUser?.email

        if (selectedImageUri != null && name.isNotEmpty() && surname.isNotEmpty() && userEmail != null) {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("images/$fileName")

            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Gönderi verilerini içeren bir harita oluşturulur
                        val scientist = hashMapOf(
                            "name" to name,
                            "surname" to surname,
                            "birthPlace" to birthPlace,
                            "birthDate" to birthDate,
                            "deathDate" to deathDate,
                            "contribution" to contribution,
                            "imageUrl" to uri.toString(),
                            "userEmail" to userEmail, // Kullanıcı e-posta adresi
                            "timestamp" to com.google.firebase.Timestamp.now() // Zaman damgası
                        )

                        firestore.collection("scientists").add(scientist)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Bilim insanı kaydedildi!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomeActivity::class.java)) // HomeActivity'ye yönlendirme
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Hata: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Resim yüklenemedi: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Lütfen tüm bilgileri doldurun ve bir resim seçin!", Toast.LENGTH_SHORT).show()
        }
    }
}
