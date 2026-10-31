package au.edu.jcu.assessment.utilityapp.data

class QuoteRepository(private val service: QuoteService) {
    suspend fun getRandomQuote(): Quote = service.randomQuote()
}
