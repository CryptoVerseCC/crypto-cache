package io.userfeeds.cryptocache

import com.palantir.docker.compose.DockerComposeRule
import io.userfeeds.cryptocache.cryptoverse.main.common.ContractsProvider
import org.assertj.core.api.Assertions.*
import org.junit.ClassRule
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@Ignore
@SpringBootTest(classes = [CryptoCacheApp::class])
@RunWith(SpringJUnit4ClassRunner::class)
class CryptoCacheAppTest {

    companion object {
        @ClassRule
        @JvmField
        val docker: DockerComposeRule = DockerComposeRule.builder()
                .file("src/test/resources/docker-compose.yml")
                .build()
    }

    @Autowired
    lateinit var contractsProvider: ContractsProvider

    @Test
    fun shouldPass() = Unit

    @Test
    fun shouldUpdateContractMapping() {
        assertThat(contractsProvider.update())
                .isEqualTo("Patryk wszystko poszło ok")
    }
}