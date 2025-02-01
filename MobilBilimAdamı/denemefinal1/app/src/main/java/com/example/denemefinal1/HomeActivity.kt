package com.example.denemefinal1

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    // RecyclerView ve Firebase nesnelerinin tanımlanması
    private lateinit var recyclerView: RecyclerView
    private lateinit var scientistList: ArrayList<Scientist> // Bilim insanı listesini tutar
    private lateinit var adapter: ScientistAdapter // RecyclerView için adapter
    private val firestore = FirebaseFirestore.getInstance() // Firestore referansı
    private val auth = FirebaseAuth.getInstance() // Firebase Authentication referansı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Kullanıcı oturum kontrolü5
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Eğer oturum açan kullanıcı yoksa MainActivity'ye yönlendirilir
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Bu aktiviteyi sonlandır
            return
        }

        setContentView(R.layout.activity_home)

        // RecyclerView yapılandırması
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        scientistList = ArrayList() // Boş bir liste başlatılır
        adapter = ScientistAdapter(scientistList) // Adapter tanımlanır
        recyclerView.adapter = adapter

        // Firestore'dan veriler yüklenir
        loadScientists()
    }

    /**
     * Firestore'dan bilim insanı verilerini yükler ve RecyclerView'da gösterir.
     */
    private fun loadScientists() {
        firestore.collection("scientists")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // Zaman damgasına göre sıralama (en yeni en üstte)
            .get()
            .addOnSuccessListener { documents ->
                scientistList.clear() // Liste temizlenir
                for (document in documents) {
                    // Firestore'daki her bir belge Scientist nesnesine dönüştürülür
                    val scientist = document.toObject(Scientist::class.java)
                    scientistList.add(scientist) // Listeye eklenir
                }
                adapter.notifyDataSetChanged() // Adapter güncellenir
            }
            .addOnFailureListener { e ->
                e.printStackTrace() // Hata durumunda hata loglanır
            }
    }

    /**
     * Menü oluşturulur (üstteki 3 nokta menüsü).
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    /**
     * Menüdeki öğeler seçildiğinde yapılacak işlemler.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_post -> {
                // Gönderi ekleme aktivitesine yönlendirme
                val intent = Intent(this, PostAddActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_logout -> {
                // Oturumu kapatma işlemi
                auth.signOut() // Firebase oturumu kapatılır
                val intent = Intent(this, MainActivity::class.java) // MainActivity'ye yönlendirilir
                startActivity(intent)
                finish() // Bu aktivite sonlandırılır
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
