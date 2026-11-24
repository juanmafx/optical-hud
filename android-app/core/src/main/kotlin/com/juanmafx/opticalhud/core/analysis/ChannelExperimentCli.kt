package com.juanmafx.opticalhud.core.analysis

import java.util.Locale
import kotlin.system.exitProcess

object ChannelExperimentCli {
    const val CSV_HEADER =
        "seed,unit_ms,frame_interval_ms,message_length,jitter_fraction,jitter_sigma_ms,trials," +
            "successful_messages,message_success_rate,success_ci95_lower,success_ci95_upper,character_error_rate"

    fun parse(arguments: List<String>): ExperimentConfig {
        require(arguments.size % 2 == 0) { "Arguments must be --name value pairs" }
        val options = arguments.chunked(2).associate { (name, value) -> name to value }
        val unknown = options.keys - setOf("--seed", "--trials", "--unit-ms", "--frame-interval-ms")
        require(unknown.isEmpty()) { "Unknown options: ${unknown.joinToString()}" }
        val defaults = ExperimentConfig()
        return defaults.copy(
            seed = options["--seed"]?.toLong() ?: defaults.seed,
            trialsPerPoint = options["--trials"]?.toInt() ?: defaults.trialsPerPoint,
            unitMs = options["--unit-ms"]?.toLong() ?: defaults.unitMs,
            frameIntervalMs = options["--frame-interval-ms"]?.toDouble() ?: defaults.frameIntervalMs
        )
    }

    fun toCsv(config: ExperimentConfig, points: List<ExperimentPoint>): String =
        (listOf(CSV_HEADER) + points.map { point ->
            String.format(
                Locale.ROOT,
                "%d,%d,%.4f,%d,%.2f,%.2f,%d,%d,%.6f,%.6f,%.6f,%.6f",
                config.seed,
                config.unitMs,
                config.frameIntervalMs,
                config.messageLength,
                point.jitterFraction,
                point.jitterSigmaMs,
                point.trials,
                point.successfulMessages,
                point.messageSuccessRate,
                point.successInterval.lower,
                point.successInterval.upper,
                point.characterErrorRate
            )
        }).joinToString("\n", postfix = "\n")
}

fun main(arguments: Array<String>) {
    val config = try {
        ChannelExperimentCli.parse(arguments.toList())
    } catch (error: IllegalArgumentException) {
        System.err.println("Invalid arguments: ${error.message}")
        exitProcess(2)
    }
    print(ChannelExperimentCli.toCsv(config, ChannelExperiment(config).run()))
}
