package com.guali.tutor.model

data class ResultadoCompilacion(val exitoso: Boolean, val errores: List<String>)

object Compilador {

    private val printlnLiteral = Regex("""IO\.println\("((?:[^"\\]|\\.)*)"\);""")

    fun compilar(codigo: String): ResultadoCompilacion {
        if (codigo.isBlank()) {
            return ResultadoCompilacion(false, listOf("error: el editor está vacío"))
        }
        val errores = mutableListOf<String>()
        var llaves = 0
        codigo.lines().forEachIndexed { indice, linea ->
            val limpia = linea.trim()
            llaves += limpia.count { it == '{' } - limpia.count { it == '}' }
            val terminaBien = limpia.isEmpty() || limpia.endsWith(";") || limpia.endsWith("{") || limpia.endsWith("}")
            if (!terminaBien) {
                errores += "Main.java:${indice + 1}: error: ';' expected"
            }
        }
        if (llaves != 0) {
            errores += "Main.java: error: reached end of file while parsing"
        }
        return ResultadoCompilacion(errores.isEmpty(), errores)
    }

    fun ejecutar(codigo: String): List<String> =
        printlnLiteral.findAll(codigo).map { it.groupValues[1] }.toList()
}
