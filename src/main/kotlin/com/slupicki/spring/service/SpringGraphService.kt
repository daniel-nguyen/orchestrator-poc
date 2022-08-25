package com.slupicki.spring.service

import com.slupicki.spring.model.OnboardingEvent.RETRY
import com.slupicki.spring.model.OnboardingEvent.SUCCESS
import com.slupicki.spring.model.OnboardingState
import com.slupicki.spring.model.OnboardingTransition
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import org.springframework.stereotype.Service
import java.io.OutputStream

@Service
class SpringGraphService {
    fun createRenderable(selected: OnboardingState?): String {
        val nodes = OnboardingState.values()
            .map { "${it.name} [label=\"${it.label}\" shape=\"ellipse\"${if (it==selected) " style=\"filled\"" else ""}]" }
            .joinToString("\n")

        val transitions = OnboardingTransition.values()
            .map { "${it.sourceState.name} -> ${it.targetState.name} [label=\"${it.event.name}\" color=${when(it.event) { SUCCESS -> "green"; RETRY -> "blue"; else -> "red"} }]" }
            .joinToString("\n")

        return "digraph G {\n{\n$nodes}\n$transitions}\n"
    }

    fun render(renderable: String): String {
        val outputStream = StringOutputStream()
        Graphviz.fromString(renderable).render(Format.SVG).toOutputStream(outputStream)
        return outputStream.toString()
    }

    private class StringOutputStream : OutputStream() {
        private val buffer = StringBuilder()

        override fun write(b: Int) {
            buffer.append(b.toChar())
        }

        override fun toString() = buffer.toString()
    }
}