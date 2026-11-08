package com.guali.tutor

import com.guali.tutor.model.Compilador
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CompiladorTest {

    @Test
    fun codigoCorrectoCompilaSinErrores() {
        val resultado = Compilador.compilar(
            """
            void main() {
                IO.println("Hola, Guali con Java 25!");
            }
            """.trimIndent()
        )
        assertTrue(resultado.exitoso)
        assertTrue(resultado.errores.isEmpty())
    }

    @Test
    fun faltaPuntoYComaReportaLaLinea() {
        val resultado = Compilador.compilar(
            """
            void main() {
                IO.println("Aprendiendo a leer errores");
                IO.println("Falta el punto y coma")
            }
            """.trimIndent()
        )
        assertFalse(resultado.exitoso)
        assertEquals(listOf("Main.java:3: error: ';' expected"), resultado.errores)
    }

    @Test
    fun llavesSinCerrarEsError() {
        val resultado = Compilador.compilar("void main() {\n    IO.println(\"x\");")
        assertFalse(resultado.exitoso)
        assertEquals(listOf("Main.java: error: reached end of file while parsing"), resultado.errores)
    }

    @Test
    fun editorVacioEsError() {
        val resultado = Compilador.compilar("   ")
        assertFalse(resultado.exitoso)
        assertEquals(listOf("error: el editor está vacío"), resultado.errores)
    }

    @Test
    fun ejecutarDevuelveLosTextosDeIOPrintln() {
        val salida = Compilador.ejecutar(
            """
            void main() {
                IO.println("Hola");
                IO.println("Guali con Java 25!");
            }
            """.trimIndent()
        )
        assertEquals(listOf("Hola", "Guali con Java 25!"), salida)
    }
}
