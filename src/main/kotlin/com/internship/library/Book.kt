package com.internship.library

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Book(
    @Id
    val id: Long,
    var title: String,
    var description: String,
    var author: String,
    var isbn: String,
    var printYear: Int,
    var readAlready: Boolean)

data class ReadAlready(val value: Boolean)