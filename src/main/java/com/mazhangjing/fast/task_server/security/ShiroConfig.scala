package com.mazhangjing.fast.task_server.security

import java.util._

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect
import org.apache.shiro.authc._
import org.apache.shiro.authc.pam.ModularRealmAuthenticator
import org.apache.shiro.mgt.SecurityManager
import org.apache.shiro.realm.Realm
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor
import org.apache.shiro.spring.web.ShiroFilterFactoryBean
import org.apache.shiro.web.mgt.DefaultWebSecurityManager
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * Shiro 配置类，包括 Spring 注解支持，URL 拦截，
  */
@Configuration
class ShiroConfig {

  private val logger = LoggerFactory.getLogger(classOf[ShiroConfig])

  /**
    * Spring 注解支持，提供 Advisor 注入的 Creator
    *
    * @return 默认的 Advisor Proxy Creator
    */
  @Bean
  @ConditionalOnMissingBean def defaultAdvisorAutoProxyCreator: DefaultAdvisorAutoProxyCreator = {
    val defaultAAP = new DefaultAdvisorAutoProxyCreator
    defaultAAP.setProxyTargetClass(true)
    defaultAAP
  }

  /**
    * Spring 注解支持，提供 Advisor
    *
    * @param securityManager Shiro 安全管理器
    * @return Advisor，供 Creator 自动调用
    */
  @Bean def authorizationAttributeSourceAdvisor(securityManager: SecurityManager): AuthorizationAttributeSourceAdvisor = {
    val advisor = new AuthorizationAttributeSourceAdvisor
    advisor.setSecurityManager(securityManager)
    advisor
  }

  /**
    * Shiro 的 URL 拦截规则
    *
    * @param securityManager Shiro 安全管理器
    * @return 过滤器工厂类
    */
  @Bean def shiroFilterFactoryBean(securityManager: SecurityManager): ShiroFilterFactoryBean = {
    val factoryBean = new ShiroFilterFactoryBean
    factoryBean.setSecurityManager(securityManager)
    //如果没有登录，则跳转 /nologin 返回信息，这里由于是 API 调用，因此不返回登录表单页面
    factoryBean.setLoginUrl("/nologin?type=anon")
    factoryBean.setUnauthorizedUrl("/nologin?type=auth")

    val map = new java.util.LinkedHashMap[String, String]
    //必须按照顺序进行拦截，对 /login, /index, /error, /static/** 这些静态、错误提醒、首页、登录页放行，其余要求身份验证
    map.put("/task/**", "roles[ADMIN]")
    map.put("/job/**", "authc")
    map.put("/**", "anon")
    factoryBean.setFilterChainDefinitionMap(map)
    factoryBean
  }

  /**
    * 提供基本的身份令牌
    *
    * @return 身份令牌
    */
  @Bean def simpleRealm = new SimpleRealm

  /**
    * 提供为令牌提供解释的身份验证器
    *
    * @param simpleRealm 身份令牌
    * @return 身份验证器
    */
  @Bean def authenticator(simpleRealm: Realm): ModularRealmAuthenticator = {
    val authenticator = new ModularRealmAuthenticator
    authenticator.setRealms(Collections.singletonList(simpleRealm))
    authenticator
  }

  /**
    * 提供 SecurityManager
    *
    * @param authenticator 身份验证器
    * @param simpleRealm   Realm 身份令牌
    * @return 嵌套了身份验证实现的 SecurityManager
    */
  @Bean def securityManager(authenticator: Authenticator, simpleRealm: Realm): SecurityManager = {
    val manager = new DefaultWebSecurityManager
    manager.setAuthenticator(authenticator)
    manager.setRealm(simpleRealm)
    manager
  }

  @Bean def shiroDialect(): ShiroDialect = new ShiroDialect()
}

