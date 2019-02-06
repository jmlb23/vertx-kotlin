package com.github.jmlb23.blog.verticles.helpers

import com.github.jmlb23.blog.domain.User

data class AddUserRequestPayload(val username: String)

object UserResponses {
  data class ErrorM(val message: String, val code: String)
  data class CDUBodyResponse(val id: Int, val message: String)

  data class GetElementResponse(val data: User)

  data class GetAllResponse(val data: List<User>)

  data class RemoveResponse(val data: CDUBodyResponse)
  data class AddResponse(val data: CDUBodyResponse)
  data class ReplaceResponse(val data: CDUBodyResponse)


  data class ErrorResponse(val data: ErrorM)
}

