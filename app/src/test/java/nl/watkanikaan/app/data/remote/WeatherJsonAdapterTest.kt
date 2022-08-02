//package nl.watkanikaan.app.data.remote
//
//import com.squareup.moshi.JsonAdapter
//import nl.watkanikaan.app.data.model.WeatherEntity
//import org.junit.Assert.*
//import org.junit.Before
//import java.lang.reflect.Type
//
//class WeatherJsonAdapterTest {
//
//    private lateinit var jsonAdapter: JsonAdapter<WeatherEntity?>
//
//    @Before
//    fun setup() {
//        val file = readJsonFile("subscriptions_v3_example.json")
//        val listType: Type = object : TypeToken<ArrayList<SubscriptionV3Json?>?>() {}.type
//        subscriptionJson = gson.fromJson(file, listType)
//    }
//
//    @Before
//    fun setUp() {
//        val networkModule = NetworkModule()
//        jsonAdapter = networkModule.createMoshi(AppNetworkModule().createMoshiBuilder())
//            .adapter(ArticleDetail::class.java)
//    }
//}