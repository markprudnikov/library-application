package com.internship.library

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod

const val EMPTY_ARRAY_STRING = "[]"
const val API_PATH = "/api/v1.0"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LibraryApplicationTests(@Autowired private val template : TestRestTemplate) {

    private val gson = Gson()
    private val HarryPotterBook = Book(
        1,
        "Harry Potter and the Philosophers Stone",
        "My favorite book",
        "Joanne Rowling",
        "ISBN-1997-1-FFF",
        1997,
        true
    )
    private val HarryPotterBook2 = Book(
        2,
        "Harry Potter and the Chamber of Secrets",
        "Second book in the collection",
        "Joanne Rowling",
        "ISBN-1998-1-FFF2",
        1998,
        false
    )
    private val HarryPotterBook3 = Book(
        3,
        "Harry Potter and the Prisoner of Azkaban",
        "Third book in the collection",
        "Joanne Rowling",
        "ISBN-1999-1-FFF2",
        1999,
        false
    )

    fun insertThreeBooks() {
        template.put("${API_PATH}/books", HarryPotterBook)
        template.put("${API_PATH}/books", HarryPotterBook2)
        template.put("${API_PATH}/books", HarryPotterBook3)
    }

    @BeforeAll
    fun `Check that there is no books in database`() {
        val response = template.getForObject("${API_PATH}/books", String::class.java)
        assertEquals(EMPTY_ARRAY_STRING, response)
    }

    @Test
    fun `Add a book, get it, set it as unread then delete it`() {
        val responseAfterPut = template.exchange("${API_PATH}/books", HttpMethod.PUT, HttpEntity(HarryPotterBook), Integer::class.java)
        val bookId = responseAfterPut.body
        val requestedBook = template.getForObject("${API_PATH}/books/${bookId}", String::class.java)
        assertEquals(gson.toJson(HarryPotterBook), requestedBook)
        template.put("${API_PATH}/books/${bookId}", ReadAlreadyBody(false))
        val bookAfterPatch = template.getForObject("${API_PATH}/books/${responseAfterPut.body}", Book::class.java)
        assertEquals(false, bookAfterPatch.readAlready)
        template.delete("${API_PATH}/books/${bookId}")
        val responseAfterDeletion = template.getForObject("${API_PATH}/books/${bookId}", String::class.java)
        assert(responseAfterDeletion.contains("404"))
    }


    @Test
    fun `Add three books, delete that not read, delete last`() {
        template.put("${API_PATH}/books", HarryPotterBook)
        template.put("${API_PATH}/books", HarryPotterBook2)
        template.put("${API_PATH}/books", HarryPotterBook3)
        val arrayOfBooks = template.getForObject("${API_PATH}/books", Array<Book>::class.java)
        assertEquals(3, arrayOfBooks.size)
        template.exchange("${API_PATH}/books/field/readAlready", HttpMethod.DELETE, HttpEntity(ReadAlreadyBody(false)), String::class.java)
        val arrOfBooksAfterDeletion = template.getForObject("${API_PATH}/books", Array<Book>::class.java)
        assertEquals(1, arrOfBooksAfterDeletion.size)
        val lastBook = arrOfBooksAfterDeletion.first()
        assertEquals(HarryPotterBook, lastBook)
        template.delete("${API_PATH}/books/${lastBook.id}")
        val response = template.getForObject("${API_PATH}/books", String::class.java)
        assertEquals(EMPTY_ARRAY_STRING, response)
    }

    @Test
    fun `Add three books, delete by year`() {
        template.put("${API_PATH}/books", HarryPotterBook)
        template.put("${API_PATH}/books", HarryPotterBook2)
        template.put("${API_PATH}/books", HarryPotterBook3)
        val arrayOfBooks = template.getForObject("${API_PATH}/books", Array<Book>::class.java)
        assertEquals(3, arrayOfBooks.size)
        template.delete("${API_PATH}/books/field/printYear/1999", HttpMethod.DELETE)
        assertEquals(2, template.getForObject("${API_PATH}/books", Array<Book>::class.java).size)
        template.delete("${API_PATH}/books/field/printYear/1998", HttpMethod.DELETE)
        assertEquals(1, template.getForObject("${API_PATH}/books", Array<Book>::class.java).size)
        template.delete("${API_PATH}/books/field/printYear/1997", HttpMethod.DELETE)
        assertEquals(0, template.getForObject("${API_PATH}/books", Array<Book>::class.java).size)
    }

    @Test
    fun `Add three books, find by year`() {
        insertThreeBooks()
        assertEquals(1, template.getForObject("${API_PATH}/books/field/printYear/1997", Array<Book>::class.java).size)
        assertEquals(1, template.getForObject("${API_PATH}/books/field/printYear/1998", Array<Book>::class.java).size)
        assertEquals(1, template.getForObject("${API_PATH}/books/field/printYear/1999", Array<Book>::class.java).size)
        template.put("${API_PATH}/books/1", ReadAlreadyBody(false))
        val arrayOfBooks = template.postForObject("${API_PATH}/books/field/readAlready", HttpEntity(ReadAlreadyBody(false)), Array<Book>::class.java)
        assertEquals(3, arrayOfBooks.size)
        template.exchange("${API_PATH}/books/field/readAlready", HttpMethod.DELETE, HttpEntity(ReadAlreadyBody(false)), String::class.java)
    }

    @Test
    fun `Delete all`() {
        insertThreeBooks()
        val arrayOfBooks = template.getForObject("${API_PATH}/books", Array<Book>::class.java)
        assertEquals(3, arrayOfBooks.size)
        template.delete("${API_PATH}/books")
        assertEquals(0, template.getForObject("${API_PATH}/books", Array<Book>::class.java).size)
    }

    @Test
    fun `Update book by id`() {
        template.put("${API_PATH}/books", HarryPotterBook)
        val bookFromRequest = template.getForObject("${API_PATH}/books/1", Book::class.java)
        assertEquals(bookFromRequest, HarryPotterBook)
        val updatedBook = Book(1, HarryPotterBook.title, HarryPotterBook.author, "It isn't my favorite...", "", 2000, false)
        template.postForObject("${API_PATH}/books", HttpEntity(updatedBook), String::class.java)
        val updatedBookRequested = template.getForObject("${API_PATH}/books/1", Book::class.java)
        assertEquals(updatedBook, updatedBookRequested)
    }
}
