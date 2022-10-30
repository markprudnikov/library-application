package com.internship.library

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import javax.transaction.Transactional

const val API_PATH = "/api/v1.0"

@RestController
@RequestMapping("${API_PATH}/books")
class BooksController(private val repository: BooksRepository) {

    @GetMapping("/")
    fun listAllBooks() = repository.findAll()

    @PutMapping("/", MediaType.APPLICATION_JSON_VALUE)
    fun addBook(@RequestBody book: Book) = repository.save(book).id

    @PostMapping("/", MediaType.APPLICATION_JSON_VALUE)
    fun updateBookById(@RequestBody book: Book) = repository.updateBookById(
        book.id,
        book.title,
        book.description,
        book.author,
        book.isbn,
        book.printYear,
        book.readAlready
    )

    @DeleteMapping("/")
    fun deleteAllBooks() = repository.deleteAll()

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long) : Book {
        return repository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Book with $id not found.")
    }

    @PutMapping("/{id}", MediaType.APPLICATION_JSON_VALUE)
    fun setAlreadyReadById(
        @PathVariable id: Long,
        @RequestBody readAlready: ReadAlready
    ) = repository.setAlreadyReadById(id, readAlready.value)

    @DeleteMapping("/{id}")
    fun deleteBookById(@PathVariable id: Long) = repository.deleteById(id)

    @GetMapping("/field/printYear/{printYear}")
    fun findBooksByPrintYear(@PathVariable printYear: Int) = repository.findBooksByPrintYear(printYear)

    @DeleteMapping("/field/printYear/{printYear}")
    fun deleteBooksByPrintYear(@PathVariable printYear: Int) = repository.deleteBooksByPrintYear(printYear)

    @PostMapping("/field/readAlready")
    fun findBooksThatAlreadyRead(@RequestBody readAlready: ReadAlready) =
        repository.findBooksByReadAlready(readAlready.value)

    @DeleteMapping("/field/readAlready", MediaType.APPLICATION_JSON_VALUE)
    fun deleteBooksByReadAlready(@RequestBody readAlready: ReadAlready) =
        repository.deleteBooksByReadAlready(readAlready.value)

}