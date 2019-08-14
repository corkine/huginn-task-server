package com.mazhangjing.fast.task_server.service

import com.mazhangjing.fast.task_server.security.UserType
import org.springframework.stereotype.Component

@Component
class LoginService {

  def getRole(username:String):String = {
    username match {
      case i if i.toUpperCase() == "CORKINE" => UserType.ADMIN.name()
      case i if i.toUpperCase().contains("MVN") => UserType.USER.name()
      case _ => UserType.NO_AUTH.name()
    }
  }

  def getPassword(username:String): String = {
    username match {
      case i if i.toUpperCase() == "CORKINE" => "spring123456"
      case i if i.toUpperCase().contains("MVN") => "mvn123456"
      case _ => ""
    }
  }

}
