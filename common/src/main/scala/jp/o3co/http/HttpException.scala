package jp.o3co.http

import spray.http.{StatusCode, StatusCodes}

//object HttpException {
//  def apply(statusCode: StatusCode, message: String = null, cause: Throwable = null): HttpException = new HttpException(statusCode, message, cause)
//
//  def apply(message: String, cause: Throwable): HttpException = new HttpException(StatusCodes.InternalServerError, message, cause)
//
//  //def apply() = new HttpException(StatusCodes.InternalServerError)
//}

case class HttpException(val statusCode: StatusCode, message: String = null, cause: Throwable = null) extends Exception(message, cause) {

  def this(message: String, cause: Throwable) = this(StatusCodes.InternalServerError, message, cause)
}
