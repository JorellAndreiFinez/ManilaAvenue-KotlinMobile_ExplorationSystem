package com.example.manilaavenue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.manilaavenue.model.CategoryModel
import com.example.manilaavenue.model.ItemsModel
import com.example.manilaavenue.model.Post
import com.example.manilaavenue.model.SliderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel: ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val _banner = MutableLiveData<List<SliderModel>>()
    private val _feature = MutableLiveData<List<SliderModel>>()


    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _bestplace = MutableLiveData<MutableList<ItemsModel>>()
    private val _allplace = MutableLiveData<MutableList<ItemsModel>>()
    private val _manilafeature = MutableLiveData<MutableList<ItemsModel>>()


    private val _posts = MutableLiveData<MutableList<Post>>()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val _itemsByCategory = MutableLiveData<Map<String, List<ItemsModel>>>()
    val itemsByCategory: LiveData<Map<String, List<ItemsModel>>> = _itemsByCategory

    val banners: LiveData<List<SliderModel>> = _banner
    val features: LiveData<List<SliderModel>> = _feature


    val category: LiveData<MutableList<CategoryModel>> = _category
    val bestPlace: LiveData<MutableList<ItemsModel>> = _bestplace
    val allplace: LiveData<MutableList<ItemsModel>> = _allplace
    val manilafeature: LiveData<MutableList<ItemsModel>> = _manilafeature

    val posts: LiveData<MutableList<Post>> = _posts

    fun loadItemsByCategory() {
        val itemsRef = firebaseDatabase.getReference("Items")
        itemsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(ItemsModel::class.java) }
                val itemsByCategory = items.groupBy { it.category }
                _itemsByCategory.postValue(itemsByCategory)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun loadBanners(){
        val Ref = firebaseDatabase.getReference("Banner")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                    _banner.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loadFeature(){
        val Ref = firebaseDatabase.getReference("FeatureToday")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<SliderModel>()
                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(SliderModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                    _feature.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loadCategory(){
        val Ref = firebaseDatabase.getReference("Category")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()

                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                    _category.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun loadBestPlace() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        if (it.rating >= 4.7) {
                            lists.add(it)
                        }
                    }
                }
                _bestplace.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun loadManilaFeature() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()
                for (childSnapshot in snapshot.children) {
                    val item = childSnapshot.getValue(ItemsModel::class.java)
                    item?.let {
                        if (it.location.contains("Intramuros", ignoreCase = true)) {
                            lists.add(it)
                        }
                    }
                }
                _manilafeature.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun loadAllPlace(){
        val Ref = firebaseDatabase.getReference("Items")
        Ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()

                for(childSnapshot in snapshot.children){
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                    _allplace.value = lists
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



    fun loadPostItems() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val userId = auth.currentUser?.uid ?: return
        val postsRef = database.reference.child("users").child(userId).child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (postSnapshot in snapshot.children) {
                    val title = postSnapshot.child("title").getValue(String::class.java) ?: ""
                    val description = postSnapshot.child("description").getValue(String::class.java) ?: ""
                    val timestamp = postSnapshot.child("timestamp").getValue(Long::class.java) ?: 0
                    val imageUrls = mutableListOf<String>()
                    val imageUrlsSnapshot = postSnapshot.child("imageUrls")
                    for (urlSnapshot in imageUrlsSnapshot.children) {
                        val imageUrl = urlSnapshot.getValue(String::class.java) ?: ""
                        if (imageUrl.isNotEmpty()) {
                            imageUrls.add(imageUrl)
                        }
                    }

                    // Convert timestamp to a readable date-time format
                    val formattedDateTime = convertTimestampToDate(timestamp)

                    val post = Post(title, description, imageUrls, timestamp)
                    posts.add(post)
                }
                _posts.value = posts
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(date)
    }




}