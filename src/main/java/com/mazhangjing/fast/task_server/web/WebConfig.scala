package com.mazhangjing.fast.task_server.web

import com.mazhangjing.fast.task_server.security.CorsList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.{CorsRegistry, DefaultServletHandlerConfigurer, ViewControllerRegistry, WebMvcConfigurer}

@Configuration
class WebConfig extends WebMvcConfigurer {

  @Autowired var cors: CorsList = _

  override def configureDefaultServletHandling(configurer: DefaultServletHandlerConfigurer): Unit =
    configurer.enable()

  override def addViewControllers(registry: ViewControllerRegistry): Unit = {
    registry.addViewController("/").setViewName("index.html")
  }
  override def addCorsMappings(registry: CorsRegistry): Unit = {
    import scala.collection.JavaConverters._
    registry.addMapping("/**").allowCredentials(true)
      .allowedHeaders("*").allowedOrigins(cors.getAllowedUrl.asScala: _*)
      .maxAge(3600)
  }

}

