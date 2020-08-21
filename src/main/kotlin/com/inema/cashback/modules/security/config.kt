package com.inema.cashback.modules.security

import com.inema.cashback.modules.security.authn.CashbackDetailsService
import com.inema.cashback.modules.security.authz.UserPermissions
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.web.servlet.invoke
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.io.Serializable


@EnableWebSecurity
@Configuration
class CashbackSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http {
            authorizeRequests {
                authorize("api/v1.0/auth/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            csrf {
                disable()
            }
            httpBasic {

            }
            headers {
                frameOptions {
                    disable()
                }
            }
        }
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/h2-console/**")
    }

    @Bean
    override fun userDetailsService(): UserDetailsService =
            CashbackDetailsService()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder())
    }


    @Bean
    fun permissionEvaluator() = HasPermissionEvaluator()
}

class HasPermissionEvaluator : PermissionEvaluator {
    override fun hasPermission(auth: Authentication, target: Any, permission: Any): Boolean =
            UserPermissions.select {
                (UserPermissions.sid eq auth.name)
                        .and(UserPermissions.target eq target.toString())
                        .and(UserPermissions.permission eq permission.toString())
            }.count() > 0L


    override fun hasPermission(auth: Authentication,
                               id: Serializable,
                               entity: String,
                               permission: Any
    ): Boolean =
            UserPermissions.select {
                (UserPermissions.sid eq auth.name)
                        .and(UserPermissions.targetId eq id.toString())
                        .and(UserPermissions.target eq entity)
                        .and(UserPermissions.permission eq permission.toString())
            }.count() > 0L
}