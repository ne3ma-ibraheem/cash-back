package com.inema.cashback.modules.users

import com.inema.cashback.modules.users.forms.RegistrationForm
import com.inema.cashback.utils.BaseController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/v1.0/auth")
class AuthController(val users: UsersMutationService) : BaseController {

    @PostMapping("registration")
    fun register(@RequestBody form: RegistrationForm) =
            users.createUser(form).response()

}