package com.github.jmlb23.blog.domain

import java.time.LocalDate
import java.util.*

data class Post(val id: Long,
                val content: String,
                val date: String,
                val comments: List<Comment>)

data class User(val id: Long,
                val username: String,
                val posts: List<Post>)

data class Comment(val id: Long,
                   val content: String,
                   val dateRegistered: String)

data class Login(val username: String, val password: String)
