package com.example.denemefinal1

// Gerekli kütüphaneler import ediliyor
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// MainActivity, kullanıcı oturum kontrolü, kayıt ve giriş işlemleri için tasarlanmış bir aktivitedir.
class MainActivity : AppCompatActivity() {

    // Firebase kimlik doğrulama ve Firestore nesneleri tanımlanıyor
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase kimlik doğrulama nesnesi başlatılıyor
        auth = FirebaseAuth.getInstance()

        // Oturum açmış bir kullanıcı olup olmadığı kontrol ediliyor
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Eğer kullanıcı zaten oturum açmışsa, HomeActivity'ye yönlendiriliyor
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // MainActivity kapatılıyor
            return // Aşağıdaki kodların çalışması engelleniyor
        }

        // Layout dosyası atanıyor
        setContentView(R.layout.activity_main)

        // Firebase Firestore nesnesi başlatılıyor
        firestore = FirebaseFirestore.getInstance()

        // Layout dosyasındaki bileşenler tanımlanıyor
        val emailInput = findViewById<EditText>(R.id.emailInput) // Kullanıcının email girişi
        val passwordInput = findViewById<EditText>(R.id.passwordInput) // Kullanıcının şifre girişi
        val registerButton = findViewById<Button>(R.id.registerButton) // Kayıt ol butonu
        val loginButton = findViewById<Button>(R.id.loginButton) // Giriş yap butonu

        // Kayıt ol butonuna tıklama dinleyicisi atanıyor
        registerButton.setOnClickListener {
            val email = emailInput.text.toString() // Email inputu alınıyor
            val password = passwordInput.text.toString() // Şifre inputu alınıyor

            // E-posta ve şifre alanlarının boş olup olmadığı kontrol ediliyor
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Eğer her iki alan doluysa kayıt işlemi başlatılıyor
                registerUser(email, password)
            } else {
                // Eğer alanlar boşsa kullanıcıya uyarı mesajı gösteriliyor
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }

        // Giriş yap butonuna tıklama dinleyicisi atanıyor
        loginButton.setOnClickListener {
            val email = emailInput.text.toString() // Email inputu alınıyor
            val password = passwordInput.text.toString() // Şifre inputu alınıyor

            // E-posta ve şifre alanlarının boş olup olmadığı kontrol ediliyor
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Eğer her iki alan doluysa giriş işlemi başlatılıyor
                loginUser(email, password)
            } else {
                // Eğer alanlar boşsa kullanıcıya uyarı mesajı gösteriliyor
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Kullanıcı kaydı yapmak için kullanılan fonksiyon
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Kayıt başarılıysa, kullanıcı bilgileri Firestore'a kaydediliyor
                    saveUserToFirestore(email)
                } else {
                    // Kayıt başarısızsa hata mesajı gösteriliyor
                    Toast.makeText(
                        this,
                        "Kayıt başarısız: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    // Kullanıcı bilgilerini Firestore veritabanına kaydetmek için kullanılan fonksiyon
    private fun saveUserToFirestore(email: String) {
        val userId = auth.currentUser?.uid // Oturum açan kullanıcının UID'si alınıyor
        if (userId != null) {
            // Kullanıcı bilgileri bir HashMap olarak oluşturuluyor
            val user = hashMapOf(
                "email" to email,
                "name" to "Kullanıcı Adı" // Varsayılan olarak bir isim atanıyor (gerekirse kullanıcıdan alınabilir)
            )
            // Kullanıcı bilgileri Firestore'a kaydediliyor
            firestore.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    // Kayıt başarılı olduğunda mesaj gösteriliyor
                    Toast.makeText(this, "Kullanıcı başarıyla kaydedildi!", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener {
                    // Kayıt başarısız olduğunda mesaj gösteriliyor
                    Toast.makeText(this, "Kullanıcı kaydedilemedi!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Kullanıcı giriş yapmak için kullanılan fonksiyon
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Eğer giriş başarılıysa HomeActivity'ye yönlendirme yapılıyor
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // MainActivity kapatılıyor
                } else {
                    // Giriş başarısızsa hata mesajı gösteriliyor
                    Toast.makeText(
                        this,
                        "Giriş başarısız: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
