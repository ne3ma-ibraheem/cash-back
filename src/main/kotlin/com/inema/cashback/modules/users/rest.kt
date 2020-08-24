package com.inema.cashback.modules.users

import com.inema.cashback.utils.BaseController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1.0/auth")
class AuthController(val users: UserMutationService) : BaseController {

    @PostMapping("registration")
    fun register(@RequestBody form: RegistrationForm) =
            users.createUser(form).response()

}