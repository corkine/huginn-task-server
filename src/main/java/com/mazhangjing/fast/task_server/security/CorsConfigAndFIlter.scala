package com.mazhangjing.fast.task_server.security

import java.io.IOException
import java.util

import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import scala.beans.BeanProperty

/**
  * 提供过滤列表，方便在 CorsFilter 和 APIConfiguration 中使用进行 Cors 的过滤
  *
  * @param allowedUrl yml 配置类中允许 Cors 的 URL 列表
  */
@Component
@ConfigurationProperties(prefix = "shiro")
class CorsList(@BeanProperty var allowedUrl: util.List[String])

/**
  * 过滤 Http 请求，以允许限制的跨域访问
  */
@Component
class CorsFilter extends Filter {

  @Autowired var cors: CorsList = _

  private val logger: Logger = LoggerFactory.getLogger(classOf[CorsFilter])

  @throws[ServletException]
  override def init(filterConfig: FilterConfig): Unit = {}

  @throws[IOException]
  @throws[ServletException]
  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain): Unit = {
    val response: HttpServletResponse = servletResponse.asInstanceOf[HttpServletResponse]
    val request: HttpServletRequest = servletRequest.asInstanceOf[HttpServletRequest]
    val originFromHeader: String = request.getHeader("Origin")
    if (originFromHeader != null && cors.getAllowedUrl.contains(originFromHeader)) {
      response.setHeader("Access-Control-Allow-Origin", originFromHeader)
      response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
      response.setHeader("Access-Control-Allow-Credentials", "true")
    }
    filterChain.doFilter(servletRequest, servletResponse)
  }

  override def destroy(): Unit = {
  }
}

