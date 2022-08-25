package com.slupicki.spring

import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.model.StateMachine
import com.slupicki.orchestrator.model.StateMachineType
import com.slupicki.orchestrator.service.GraphService
import com.slupicki.orchestrator.service.StateMachineEngine
import com.slupicki.orchestrator.service.event.Bus
import com.slupicki.orchestrator.service.event.EventBus
import com.slupicki.orchestrator.service.event.EventInContext
import com.slupicki.spring.model.Onboarding
import com.slupicki.spring.model.OnboardingEvent
import com.slupicki.spring.model.OnboardingTransition
import com.slupicki.spring.repository.OnboardingRepository
import com.slupicki.spring.service.OnboardingService
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class SpringWebController(
    private val onboardingRepository: OnboardingRepository,
    private val onboardingService: OnboardingService) {
    val log = KotlinLogging.logger {}

    @RequestMapping("/")
    fun home(model: Model): String {
        val stateMachineTypes: List<StateMachineType> = listOf(StateMachineType(1L, "ONBOARDING", listOf()))
        val stateMachines: List<Onboarding> = onboardingRepository.findAll().collectList().block()!!
        val machineIdToAvailableEvents: Map<String, List<OnboardingEvent>> = stateMachines.associate { onboarding -> onboarding.id.toString() to
                OnboardingTransition.values()
                    .filter { it.sourceState.name == onboarding.state }
                    .map { it.event }
                    .distinct()
                    .sorted()}
        model.addAttribute("stateMachines", stateMachines)
        model.addAttribute("machineIdToAvailableEvents", machineIdToAvailableEvents)
        model.addAttribute("stateMachineTypes", stateMachineTypes)
        return "spring_state"
    }

    @GetMapping("/create_machine")
    fun createMachine(): String {
        onboardingService.createOnboarding().block()
        // To give backend time to stabilize after event
        Thread.sleep(1000)
        return "redirect:/"
    }

    @GetMapping("/send_event")
    fun sendEvent(@RequestParam event: OnboardingEvent, @RequestParam(name = "machine_id") machineId: Long): String {
        onboardingService.handleEvent(event, machineId).collectList().block()!!
        // To give backend time to stabilize after event
        Thread.sleep(1000)
        return "redirect:/"
    }


}