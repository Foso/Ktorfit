package de.jensklingenberg.ktorfit

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

class KtorfitLogger(private val kspLogger: KSPLogger, private val loggingType: Int) : KSPLogger by kspLogger {
    override fun error(
        message: String,
        symbol: KSNode?,
    ) {
        when (loggingType) {
            0 -> {
                // Do nothing
            }

            1 -> {
                // Throw compile errors for Ktorfit
                kspLogger.error("Ktorfit: $message", symbol)
            }

            2 -> {
                // Turn errors into compile warnings
                kspLogger.warn("Ktorfit: $message", symbol)
            }
        }
    }
}
