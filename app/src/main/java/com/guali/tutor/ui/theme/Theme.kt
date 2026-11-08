package com.guali.tutor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaOscuro = darkColorScheme(
    primary = AzulGualiClaro,
    secondary = GrisAzuladoClaro,
    tertiary = VerdeGualiClaro
)

private val EsquemaClaro = lightColorScheme(
    primary = AzulGuali,
    secondary = GrisAzulado,
    tertiary = VerdeGuali
)

@Composable
fun GualiTutorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}
