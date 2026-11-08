package com.guali.tutor.model

enum class Objetivo(val nombre: String) {
    COMPILAR_CON_ERROR("Compilar con error"),
    COMPILAR_EXITOSO("Compilar exitoso"),
    EJECUTAR_CON_ERROR("Ejecutar con error"),
    EJECUTAR_EXITOSO("Ejecutar exitoso")
}

data class Leccion(
    val nombre: String,
    val objetivo: Objetivo,
    val contenido: String
)

object LeccionesDeEjemplo {

    val todas = listOf(
        Leccion(
            nombre = "leccion-01-hola-mundo",
            objetivo = Objetivo.COMPILAR_EXITOSO,
            contenido = """
                Lección 1: Hola Mundo

                Copia el siguiente código en el editor y presiona Compilar:

                void main() {
                    IO.println("Hola, Guali con Java 25!");
                }

                El objetivo se cumple cuando el compilador reporta 0 errores.
            """.trimIndent()
        ),
        Leccion(
            nombre = "leccion-02-errores-compilacion",
            objetivo = Objetivo.COMPILAR_CON_ERROR,
            contenido = """
                Lección 2: Errores de compilación

                Copia el código tal cual. A la segunda instrucción le falta el punto y coma:

                void main() {
                    IO.println("Aprendiendo a leer errores");
                    IO.println("Falta el punto y coma")
                }

                El objetivo se cumple cuando el compilador reporta al menos un error.
            """.trimIndent()
        ),
        Leccion(
            nombre = "leccion-04-variables-y-ciclos",
            objetivo = Objetivo.EJECUTAR_EXITOSO,
            contenido = """
                Lección 4: Variables y ciclos

                void main() {
                    int numero = 7;
                    for (int i = 1; i <= 10; i++) {
                        IO.println(numero + " x " + i + " = " + (numero * i));
                    }
                }

                Compila y ejecuta. El objetivo se cumple cuando el programa termina sin errores.
            """.trimIndent()
        )
    )
}
