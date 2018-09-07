package io.userfeeds.cryptocache.erc20

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenInfoController(private val repository: TokenInfoRepository) {

    @RequestMapping("/token_info")
    fun tokenInfo(@RequestParam("ids") ids: List<String>): Map<String, TokenInfo> {
        return repository.get(ids.toSet())
    }
}
