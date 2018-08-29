package com.kyotob.api.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
  @RequestMapping(value = ["/ping"], method = [RequestMethod.GET], produces = ["application/json"])
  fun index() = "{\"message\" : \"It works!\"}"
}