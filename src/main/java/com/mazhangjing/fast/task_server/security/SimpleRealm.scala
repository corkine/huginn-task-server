package com.mazhangjing.fast.task_server.security

import java.util.Collections

import com.mazhangjing.fast.task_server.service.LoginService
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc._
import org.apache.shiro.authz.{AuthorizationInfo, SimpleAuthorizationInfo}
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.springframework.beans.factory.annotation.Autowired

/**
  * 提供 Realm 服务
  */
class SimpleRealm extends AuthorizingRealm {

  @Autowired var service: LoginService = _

  override protected def doGetAuthorizationInfo(principalCollection: PrincipalCollection): AuthorizationInfo = {
    val userName = SecurityUtils.getSubject.getPrincipal.asInstanceOf[String]
    val info = new SimpleAuthorizationInfo
    val role = service.getRole(userName)
    info.setRoles(Collections.singleton(role))
    info
  }

  @throws[AuthenticationException]
  override protected def doGetAuthenticationInfo(authenticationToken: AuthenticationToken): AuthenticationInfo = {
    val token = authenticationToken.asInstanceOf[UsernamePasswordToken]
    val password = service.getPassword(token.getUsername)
    try {
      if (null == password)
        throw new AccountException("不存在此用户")
      else if (!(password == new String(token.getCredentials.asInstanceOf[Array[Char]])))
        throw new AccountException("密码错误")
    } catch {
      case _: Exception =>
    }
    new SimpleAuthenticationInfo(token.getPrincipal, password, getName)
  }
}

