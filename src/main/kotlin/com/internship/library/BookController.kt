package com.internship.library

import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

const val API_PATH = "/api/v1.0"

@RestController
@RequestMapping(API_PATH)
class BooksController(private val repository: BooksRepository) {

    @GetMapping("/books")
    fun listAllBooks() = repository.findAll()

    @PutMapping("/books", MediaType.APPLICATION_JSON_VALUE)
    fun addBook(@RequestBody book: Book) = repository.save(book).id

    @PostMapping("/books", MediaType.APPLICATION_JSON_VALUE)
    fun updateBookById(@RequestBody book: Book) = repository.updateBookById(
        book.id,
        book.title,
        book.description,
        book.author,
        book.isbn,
        book.printYear,
        book.readAlready
    )

    @DeleteMapping("/books")
    fun deleteAllBooks() = repository.deleteAll()

    @GetMapping("/books/{id}")
    fun getBookById(@PathVariable id: Long) : Book {
        return repository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Book with $id not found.")
    }

    @PutMapping("/books/{id}", MediaType.APPLICATION_JSON_VALUE)
    fun setAlreadyReadById(
        @PathVariable id: Long,
        @RequestBody body: ReadAlreadyBody
    ) = repository.setAlreadyReadById(id, body.readAlready)

    @DeleteMapping("/books/{id}")
    fun deleteBookById(@PathVariable id: Long) = repository.deleteById(id)

    @GetMapping("/books/field/printYear/{printYear}")
    fun findBooksByPrintYear(@PathVariable printYear: Int) = repository.findBooksByPrintYear(printYear)

    @DeleteMapping("/books/field/printYear/{printYear}")
    fun deleteBooksByPrintYear(@PathVariable printYear: Int) = repository.deleteBooksByPrintYear(printYear)

    @PostMapping("/books/field/readAlready")
    fun findBooksThatAlreadyRead(@RequestBody body: ReadAlreadyBody) =
        repository.findBooksByReadAlready(body.readAlready)

    @DeleteMapping("/books/field/readAlready", MediaType.APPLICATION_JSON_VALUE)
    fun deleteBooksByReadAlready(@RequestBody body: ReadAlreadyBody) =
        repository.deleteBooksByReadAlready(body.readAlready)

}