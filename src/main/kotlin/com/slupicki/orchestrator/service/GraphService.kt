package com.slupicki.orchestrator.service

import com.slupicki.orchestrator.model.Event
import com.slupicki.orchestrator.model.State
import com.slupicki.orchestrator.model.StateMachineType
import com.slupicki.orchestrator.model.Transition
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.OutputStream

@Service
class GraphService(
//    val stateMachineTypeRepository: StateMachineTypeRepository,
) {
    val log = KotlinLogging.logger {}

    val svgCache = mutableMapOf<String, String>()

    fun String.dotSafe() = this.replace(" ", "_")

    @Transactional
    fun createDot(type: String, markState: String = ""): String {
//        val stateMachineType = stateMachineTypeRepository.findByName(type)
        val stateMachineType = StateMachineType(1, "", emptyList())
        val transitions = stateMachineType.transitions
        val states = transitions.flatMap { listOf(it.fromState, it.toState) }.toSet()
        return """
            digraph G {
                {
${states.joinToString("\n") { stateToDotNode(it, markState) }}
                }
${transitions.joinToString("\n") { transitionToDotEdge(it) }}
            }
        """.trimIndent()
    }

    fun  dotToSvg(dot: String): String {
        if (svgCache.containsKey(dot)) {
            return svgCache[dot]!!
        }
        val myOutputStream = StringOutputStream()
        Graphviz.fromString(dot).render(Format.SVG).toOutputStream(myOutputStream)
        val svg = myOutputStream.toString()
        svgCache[dot] = svg
        return svg
    }

    fun transitionToDotEdge(t: Transition): String = """
        S${t.fromState.id} -> S${t.toState.id} [label="${t.event}" color=${when(t.event) {
            Event.SUCCESS, Event.UNKNOWN -> "black"
            Event.FAILURE -> "red"
        }}]
    """.trimIndent()

    fun stateToDotNode(state: State, markState: String = ""): String = """
        S${state.id} [label="${state.name}" ${
        when {
            state.initial -> "shape=invhouse"
            state.final -> "shape=house"
            else -> "shape=ellipse"
        }
    } ${if (state.name == markState) "style=filled" else "" }]
    """.trimIndent()


    class StringOutputStream() : OutputStream() {
        private val s = StringBuilder()

        override fun write(b: Int) {
            s.append(b.toChar())
        }

        override fun toString() = s.toString()
    }
}