package com.example.denemefinal1

// Gerekli kütüphaneler import ediliyor
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

// LoginActivity, kullanıcı giriş işlemlerini yönetmek için tasarlanmış bir aktivitedir.
class LoginActivity : AppCompatActivity() {
    // Firebase kimlik doğrulama nesnesi
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Aktivitenin layout dosyası atanıyor
        setContentView(R.layout.activity_login)

        // Firebase kimlik doğrulama nesnesi başlatılıyor
        auth = FirebaseAuth.getInstance()

        // Layout dosyasındaki bileşenler tanımlanıyor
        val emailInput = findViewById<EditText>(R.id.emailInput) // Kullanıcının email girişi
        val passwordInput = findViewById<EditText>(R.id.passwordInput) // Kullanıcının şifre girişi
        val loginButton = findViewById<Button>(R.id.loginButton) // Giriş yap butonu

        // Giriş butonuna tıklama dinleyicisi atanıyor
        loginButton.setOnClickListener {
            // Kullanıcıdan alınan e-posta ve şifre değerleri
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // E-posta ve şifre alanlarının boş olup olmadığı kontrol ediliyor
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Eğer her iki alan da doluysa giriş işlemi başlatılıyor
                loginUser(email, password)
            } else {
                // Eğer alanlar boşsa kullanıcıya uyarı mesajı gösteriliyor
                Toast.makeText(this, "Lütfen tüm alanları doldurun!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Kullanıcıyı Firebase Authentication ile giriş yapmaya çalıştıran fonksiyon
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Eğer giriş başarılıysa ana sayfaya yönlendirme yapılır
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // LoginActivity'yi kapatır
                } else {
                    // Eğer giriş başarısızsa hata mesajı gösterilir
                    Toast.makeText(this, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
