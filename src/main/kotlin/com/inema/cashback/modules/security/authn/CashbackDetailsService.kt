package com.inema.cashback.modules.security.authn

import com.inema.cashback.modules.users.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException


class CashbackDetailsService : UserDetailsService {
    override fun loadUserByUsername(user: String): UserDetails =
            transaction {
                Users.findByUsernameOrEmail(user).map {
                    User.builder()
                            .username(it[Users.id])
                            .password(it[Users.password])
                            .roles("USER").build()
                }.firstOrNull() ?: throw UsernameNotFoundException("there is no user with name $user")
            }
}