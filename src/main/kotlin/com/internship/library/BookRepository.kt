package com.internship.library

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import javax.transaction.Transactional

interface BooksRepository : CrudRepository<Book, Long> {

    override fun findAll() : Iterable<Book>

    override fun <S : Book> save(entity: S): S

    override fun deleteById(id: Long)

    fun findBooksByPrintYear(printYear: Int) : Iterable<Book>

    fun findBooksByReadAlready(readAlready: Boolean) : Iterable<Book>

    override fun deleteAll()

    @Modifying
    @Transactional
    @Query("delete from Book where printYear = :print_year")
    fun deleteBooksByPrintYear(@Param("print_year") printYear: Int)

    @Modifying
    @Transactional
    @Query("update Book set readAlready = :read_already where id = :book_id")
    fun setAlreadyReadById(@Param("book_id") id: Long, @Param("read_already") readAlready: Boolean)

    @Modifying
    @Transactional
    @Query("delete from Book where readAlready = :readAlready")
    fun deleteBooksByReadAlready(@Param("readAlready") readAlready: Boolean)

    @Modifying
    @Transactional
    @Query("""update Book b set 
        b.title = :title,
        b.description = :description,
        b.author = :author,
        b.isbn = :isbn,
        b.printYear = :print_year,
        b.readAlready = :read_already
            where b.id = :id""")
    fun updateBookById(
        @Param("id") id: Long,
        @Param("title") title: String,
        @Param("description") description: String,
        @Param("author") author: String,
        @Param("isbn") isbn: String,
        @Param("print_year") printYear: Int,
        @Param("read_already") readAlready: Boolean
    )
}