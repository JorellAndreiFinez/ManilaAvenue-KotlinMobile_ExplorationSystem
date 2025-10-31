package com.example.manilaavenue.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.manilaavenue.R
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.manilaavenue.model.LocationModel
import com.google.api.Distribution.BucketOptions.Linear
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider


class LocationActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_location)

        mapView = findViewById(R.id.map)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val mapController = mapView.controller
        mapController.setZoom(15.0)
        val startPoint = GeoPoint(14.5995, 120.9842) // Coordinates for Manila
        mapController.setCenter(startPoint)

        // Enable MyLocation overlay
        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        myLocationOverlay.enableFollowLocation()
        mapView.overlays.add(myLocationOverlay)

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        }

        findViewById<LinearLayout>(R.id.optionClose).setOnClickListener{
            finish()
        }

        val locations = listOf(
            LocationModel("University of Santo Tomas (UST)", 14.6094, 120.9902, "España Blvd, Sampaloc, Manila, 1008 Metro Manila", "Known for its historic campus and beautiful architecture, UST is one of the oldest universities in Asia. Near UST are many food trips to go to and sure are within reach for all students.", "Park"),
            LocationModel("Quiapo Church (Minor Basilica of the Black Nazarene)", 14.5974, 120.9842, "910 Plaza Miranda, Quiapo, Manila, 1001 Metro Manila", "A famous Catholic church known for its annual procession of the Black Nazarene statue.", "Church Memorabilia"),
            LocationModel("Divisoria", 14.6003, 120.9724, "243, Tondo, Manila, Metro Manila", "A bustling shopping district known for its bargain goods, especially clothing, accessories, and other merchandise. Divisoria has plenty of malls to go to, that’s why Divisoria is known to have everything you’re looking for.", "Market"),
            LocationModel("Quezon Memorial Circle", 14.6510, 121.0490, "Elliptical Rd, Diliman, Quezon City, Metro Manila", "A national park and shrine located in Quezon City, featuring the Quezon Memorial Shrine and various recreational facilities.", "Park"),
            LocationModel("Las Tres Marias Deliciosa Lasaña", 14.6077, 120.9901, "1177 Dapitan St, Sampaloc, Manila, 1015 Metro Manila", "A delectable lasagna dish served in Manila, featuring layers of pasta, savory Bolognese sauce, creamy béchamel, and melted cheese, offering a satisfying Italian-inspired culinary experience in the city.", "Hidden Gem"),
            LocationModel("Tigers Cove", 14.6073, 120.9888, "Padre Noval St, Sampaloc, Manila, 1015 Metro Manila", "Offers a cozy dining experience with a menu featuring a variety of Filipino and Asian dishes. Known for its warm ambiance and friendly service, it's a popular spot for students and locals alike seeking affordable and delicious meals in the area.", "Hidden Gem"),
            LocationModel("Tasty Dumplings", 14.6014, 120.9738, "Wellington Building, Norberto Ty St, Binondo, Manila, 1006 Metro Manila", "Traditional recipes and local flair blend into mouthwatering bites that captivate the senses. Taste the essence of Chinatown's culinary heritage through each perfectly crafted dumpling, served fresh and bursting with flavor.", "Hidden Gem"),
            LocationModel("Rizal Park (Luneta)", 14.5820, 120.9797, "Ermita, Manila, 1000 Metro Manila", "A historical urban park in Manila, known for its monuments, gardens, and cultural significance, including the Rizal Monument.", "Park"),
            LocationModel("Manila Ocean Park", 14.5794, 120.9724, "Quirino Grandstand, 666 Behind, Ermita, Manila, 1000 Metro Manila", "An oceanarium and marine-themed park offering interactive exhibits, animal shows, and encounters with marine creatures.", "Park"),
            LocationModel("Intramuros", 14.5869, 120.9754, "Intramuros, Manila, Philippines", "A historic walled area within Manila that features Spanish colonial architecture, churches (like San Agustin Church), museums, and Fort Santiago. Inside Intramuros are plenty of restaurants to eat from that ranges budget friendly to fancy ones.", "Park memorabilia"),
            LocationModel("La Cathedral Cafe", 14.5916, 120.9747, "636 Cabildo St, Intramuros, Manila, 1002 Metro Manila", "The Basilica Minore de San Sebastián, colloquially known as the Manila Cathedral, is one of the most famous churches in the country.", "Hidden Gem"),
            LocationModel("Binondo (food district)", 14.6021, 120.9770, "Binondo, Metro Manila", "The oldest Chinatown in the world, Binondo offers a mix of Chinese-Filipino culture, historical landmarks, and delicious foods that are surely budget friendly.", "Market"),
            LocationModel("San Agustin Church", 14.5899, 120.9757, "General Luna St, Intramuros, Manila, 1002 Metro Manila", "A UNESCO World Heritage Site within Intramuros, known for its Baroque architecture, historical significance, and beautifully preserved interiors.", "Church Memorabilia"),
            LocationModel("Barbara's Heritage Restaurant", 14.5885, 120.9739, "Plaza San Luis Complex, General Luna St, Intramuros, Manila, 1002 Metro Manila", "Found in the heart of Intramuros, along General Luna Street, Barbara's offers a cultural dining experience with traditional Filipino cuisine and live cultural performances in a charming colonial setting.", "Restaurant"),
            LocationModel("Manila Baywalk", 14.5748, 120.9721, "Roxas Blvd, Malate, Manila, 1004 Metro Manila", "A waterfront promenade along Manila Bay known for its scenic views, restaurants, and evening strolls.", "Park"),
            LocationModel("Fort Santiago", 14.5915, 120.9739, "Intramuros, Manila, 1002 Metro Manila", "A citadel located in Intramuros, known for its historical significance and Spanish-era architecture.", "Park"),
            LocationModel("Star City", 14.5615, 120.9825, "Star City, Roxas Blvd, CCP, Pasay, Metro Manila", "An amusement park offering rides, games, and themed attractions for families and thrill-seekers in Pasay City.", "Park"),
            LocationModel("Manila Yacht Club", 14.5662, 120.9827, "2351 Roxas Boulevard, Malate, Manila, Philippines", "A prestigious club offering dining, events, and yacht services with a view of Manila Bay.", "Park"),
            LocationModel("Manila Zoo", 14.5658, 120.9874, "Adriatico St, Malate, Manila, 1004 Metro Manila", "Located in Malate, it features a variety of animals and is a popular destination for families and animal lovers.", "Park"),
            LocationModel("Intramuros Golf Course", 14.5919, 120.9733, "Intramuros, Manila, 1018 Metro Manila", "A golf course located within the historic walls of Intramuros, offering a unique golfing experience with views of historical landmarks.", "Park"),
            LocationModel("Arroceros Urban Forest Park", 14.5930, 120.9817, "659 A AntonioVillegas St, Ermita, Manila, 1000 Metro Manila", "Arroceros Urban Forest Park in Manila is a green oasis amidst the bustling cityscape, celebrated as the last lung of Manila. Located along the banks of the Pasig River, it's a sanctuary for biodiversity with over 60 tree species and various bird species.", "Park"),
            LocationModel("Nobu Manila (restaurant)", 14.5327, 120.9816, "Aseana Avenue corner Macapagal Avenue, Entertainment City, Paranaque, Metro Manila, Philippines", "Located in City of Dreams Manila, Parañaque City, known for its Japanese-Peruvian fusion cuisine created by renowned chef Nobu Matsuhisa, offering panoramic views of Manila Bay.", "Restaurant"),
            LocationModel("Upside Down Museum", 14.5475, 120.9924, "Boom Na Boom Grounds CCP Complex, Roxas Blvd, Pasay, Metro Manila", "The Upside Down Museum in Manila is a unique attraction where visitors can experience mind-bending illusions and optical tricks. It features various themed rooms and exhibits designed to create the illusion of being upside down, offering an immersive and playful experience for all ages.", "Museum"),
            LocationModel("National Museum of Fine Arts", 14.5896, 120.9749, "Padre Burgos Avenue, Manila, Philippines", "Located near Rizal Park, this museum houses a collection of Filipino visual arts from the 17th century to the contemporary period.", "Museum"),
            LocationModel("Manila Hotel", 14.5812, 120.9796, "One Rizal Park, Manila, Philippines", "The Manila Hotel is a historic five-star establishment known for its iconic location overlooking Manila Bay, offering grand architecture, opulent interiors, and a rich history hosting dignitaries and celebrities since 1912.", "Restaurant & Hotel"),
            LocationModel("SM Mall of Asia (MOA)", 14.5354, 120.9824, "SM Mall of Asia, Seaside Blvd, Pasay, 1300 Metro Manila", "One of the largest shopping malls in Asia, offering a wide range of shops, restaurants, entertainment options, and an iconic seaside sunset view.", "Market"),
            LocationModel("Yuchengco Museum", 14.5560, 121.0190, "RCBC Plaza, Corner Ayala Avenue and Senator Gil J. Puyat Avenue, Makati, 1200 Metro Manila", "The museum showcases both contemporary and traditional Filipino art, as well as historical artifacts and memorabilia related to the Yuchengco family and their contributions to Philippine society. It's a notable cultural institution that offers insights into Philippine heritage and artistic expression.", "Museum"),
            LocationModel("SM by the Bay", 14.5384, 120.9793, "MOA Complex, Seaside Blvd, Pasay, 1300 Metro Manila", "Filled with thrill and excitement, this park would surely let everyone have a blast of enjoyment while having a memorable moment with family and friends. This park is full of rides and food trips that everyone can afford and enjoy.", "Park")
        )

        // Add markers to the map
        addMarkers(locations)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    myLocationOverlay.enableMyLocation()
                } else {
                    // Permission denied, show a message to the user
                }
            }
        }
    }


    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private fun addMarkers(locations: List<LocationModel>) {
        for (location in locations) {
            val marker = Marker(mapView).apply {
                position = GeoPoint(location.latitude, location.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = location.name
                snippet = location.description
                subDescription = location.address
            }
            mapView.overlays.add(marker)
        }
    }

}