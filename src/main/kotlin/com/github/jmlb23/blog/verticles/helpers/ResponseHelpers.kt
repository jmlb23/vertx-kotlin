package com.github.jmlb23.blog.verticles.helpers

import com.github.jmlb23.blog.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class AddUserRequestPayload(val username: String)

object UserResponses {
  @Serializable
  data class ErrorM(val message: String, val code: String)
  @Serializable
  data class CDUBodyResponse(val id: Int, val message: String)

  @Serializable
  data class GetElementResponse(val data: User?)

  @Serializable
  data class GetAllResponse(val data: List<User>)

  @Serializable
  data class RemoveResponse(val data: CDUBodyResponse)
  @Serializable
  data class AddResponse(val data: CDUBodyResponse)
  @Serializable
  data class ReplaceResponse(val data: CDUBodyResponse)


  data class ErrorResponse(val data: ErrorM)
}

