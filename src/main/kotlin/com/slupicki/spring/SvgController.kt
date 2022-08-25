package com.slupicki.spring

import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.repository.OnboardingRepository
import com.slupicki.spring.service.SpringGraphService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.Optional

@RestController
class SvgController(
    val onboardingRepository: OnboardingRepository,
    val springGraphService: SpringGraphService,
) {
    @GetMapping(value = ["/svg/{id}"], produces = ["image/svg+xml"])
    fun drawSvg(@PathVariable id: Long): Mono<String> =
        onboardingRepository.findById(id)
            .map { Optional.of(OnboardingState.valueOf(it.state)) }
            .onErrorResume { Mono.just(Optional.empty()) }
            .switchIfEmpty { Mono.just(Optional.empty()) }
            .map { springGraphService.createRenderable(it.orElse(null)) }
            .doOnNext { System.err.println(it) }
            .flatMap { springGraphService.render(it).toMono() }}