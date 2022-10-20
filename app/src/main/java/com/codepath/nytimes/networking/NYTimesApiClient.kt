package com.codepath.nytimes.networking

import com.codepath.nytimes.models.BestSellerBook
import com.codepath.nytimes.models.NYTimesAPIResponse
import com.codepath.nytimes.models.Article
import com.codepath.nytimes.models.NYTimesArticlesAPIResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * This code represents the networking layer of the application,
 * Other than updating the API key, you will NOT need to touch this code for the lab,
 * However it may be useful to reference the logic for your future projects
 *
 *
 * IMPORTANT INSTRUCTIONS BELOW ===========================================================
 * TODO: You have to update API_KEY variable with your own NY-Times developer api key, see
 * https://developer.nytimes.com/get-started to create your own developer account,
 * after copy and paste the API key under your Account -> Apps -> <Your App> -> API Keys
</Your> */

// TODO: Replace the below API key with your own generated key
private const val API_KEY = "kKUE00C3kHAUg3BEafvNLugIuLSvm84H"
private const val API_FILTER = "headline, web_url, snippet, pub_date, word_count, print_page, print_section, section_name"
private const val BEGIN_DATE = "20100101"
private const val SORT_BY = "relevance"

class NYTimesApiClient {
    private val nyTimesService: NYTimesService
    fun getBestSellersList(booksListResponse: CallbackResponse<List<BestSellerBook>>) {

        // this hard codes to only the current date's hardcover fiction best selling books
        // see https://developer.nytimes.com/docs/books-product/1/overview for more information on API documentation
        val current = nyTimesService.getBestSellingBooks("current", "hardcover-fiction", API_KEY)
        current?.enqueue(object : Callback<NYTimesAPIResponse?> {
            override fun onResponse(call: Call<NYTimesAPIResponse?>, response: Response<NYTimesAPIResponse?>) {
                val model = response.body()
                val books = model?.results?.books
                if (response.isSuccessful && books != null) {
                    booksListResponse.onSuccess(books)
                } else {
                    booksListResponse.onFailure(Throwable("error with response code " + response.code() + " " + response.message()))
                }
            }

            override fun onFailure(call: Call<NYTimesAPIResponse?>, t: Throwable) {
                booksListResponse.onFailure(t)
            }
        })
    }

    /**
     * gets the articles given a specific query, default page number to 0
     * @param articlesListResponse
     * @param query
     */
    fun getArticlesByQuery(articlesListResponse: CallbackResponse<List<Article>>, query: String) {
        getArticlesByQuery(articlesListResponse, query, 0)
    }

    /**
     * gets the articles given a specific query and page number
     * @param articlesListResponse
     * @param query
     * @param pageNumber
     */
    fun getArticlesByQuery(articlesListResponse: CallbackResponse<List<Article>>, query: String?, pageNumber: Int) {
        // this hard codes to only get the articles sorted by "relevance" sort order
        // you can actually alter the api query to have more search filters or change the sort order to search by "newest"
        // see https://developer.nytimes.com/docs/articlesearch-product/1/routes/articlesearch.json/get for more information on API documentation
        val current = nyTimesService.getArticlesByQuery(
                query,
                pageNumber,
                SORT_BY,
                API_FILTER,
                BEGIN_DATE,
                API_KEY)
        current?.enqueue(object : Callback<NYTimesArticlesAPIResponse?> {
            override fun onResponse(call: Call<NYTimesArticlesAPIResponse?>, response: Response<NYTimesArticlesAPIResponse?>) {
                val model = response.body()
                val docs = model?.response?.docs
                if (response.isSuccessful && docs != null) {
                    articlesListResponse.onSuccess(docs)
                } else {
                    articlesListResponse.onFailure(Throwable("error with response code " + response.code() + " " + response.message()))
                }
            }

            override fun onFailure(call: Call<NYTimesArticlesAPIResponse?>, t: Throwable?) {
                articlesListResponse.onFailure(t)
            }
        })
    }

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.nytimes.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        nyTimesService = retrofit.create(NYTimesService::class.java)
    }
}
