package com.github.jmlb23.blog.domain

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.*

@Serializable
data class Post(val id: Long,
                val content: String,
                val date: String,
                val comments: List<Comment>)
@Serializable
data class User(val id: Long,
                val username: String,
                val posts: List<Post>)
@Serializable
data class Comment(val id: Long,
                   val content: String,
                   val dateRegistered: String)

data class Login(val username: String, val password: String)
