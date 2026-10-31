package au.edu.jcu.assessment.utilityapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// The public DummyJSON endpoint requires no API key.
data class Quote(val id: Int, val quote: String, val author: String)

interface QuoteService {
    @GET("quotes/random")
    suspend fun randomQuote(): Quote

    companion object {
        fun create(): QuoteService = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteService::class.java)
    }
}
