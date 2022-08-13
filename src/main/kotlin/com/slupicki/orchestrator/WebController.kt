package com.slupicki.orchestrator

import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.service.GraphService
import com.slupicki.orchestrator.service.StateMachineEngine
import com.slupicki.orchestrator.service.event.Bus
import com.slupicki.orchestrator.service.event.EventBus
import com.slupicki.orchestrator.service.event.EventInContext
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
class WebController(
    val graphService: GraphService,
    val stateMachineEngine: StateMachineEngine,
    val eventBus: EventBus,
) {
    val log = KotlinLogging.logger {}

    @RequestMapping("/")
    fun home(model: Model): String {
        val stateMachineTypes = stateMachineEngine.getMachineTypes()
        val stateMachines = stateMachineEngine.getMachines()
        val machineIdToAvailableEvents = stateMachines.associate { it.id!! to it.transitionsFromCurrentState().keys }
        model.addAttribute("stateMachines", stateMachines)
        model.addAttribute("machineIdToAvailableEvents", machineIdToAvailableEvents)
        model.addAttribute("stateMachineTypes", stateMachineTypes)
        return "state"
    }

    @RequestMapping("/svg/{type}")
    fun graph(
        @PathVariable type: String,
        @RequestParam(defaultValue = "") markState: String,
    ): ResponseEntity<String> {
        val dot = graphService.createDot(type, markState)
        val svg = graphService.dotToSvg(dot)
        return ResponseEntity
            .ok()
            .contentType(MediaType("image", "svg+xml"))
            .body(svg)
    }

    @GetMapping("/create_machine")
    fun createMachine(
        @RequestParam(name = "machine_name") machineName: String,
        @RequestParam(name = "machine_type") machineType: String,
        model: Model,
    ): String {
        stateMachineEngine.createMachine(machineType, machineName, mapOf("log_counter" to "1"))
        return "redirect:/"
    }

    @GetMapping("/send_event")
    fun sendEvent(
        @RequestParam event: String,
        @RequestParam(name = "machine_id") machineId: String,
        model: Model,
    ): String {
        val eventForStateMachineEngine = EventInContext(
            stateMachineId = machineId,
            event = Event.valueOf(event),
        )
        eventBus.send(Bus.TO_STATE_MACHINE, eventForStateMachineEngine)
        // To give backend time to stabilize after event
        Thread.sleep(500)
        return "redirect:/"
    }


}