package com.juanmafx.opticalhud.core.analysis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ChannelExperimentTest {
    private val config = ExperimentConfig(seed = 42L, trialsPerPoint = 60)

    @Test
    fun `same seed reproduces identical results`() {
        assertEquals(ChannelExperiment(config).run(), ChannelExperiment(config).run())
    }

    @Test
    fun `different seeds change noisy results`() {
        assertNotEquals(ChannelExperiment(config).run(), ChannelExperiment(config.copy(seed = 43L)).run())
    }

    @Test
    fun `zero jitter delivers every message`() {
        val point = ChannelExperiment(config).run().first()
        assertEquals(point.trials, point.successfulMessages)
        assertEquals(0.0, point.characterErrorRate)
    }

    @Test
    fun `heavy jitter degrades delivery`() {
        val points = ChannelExperiment(config).run()
        assertTrue(points.last().messageSuccessRate < points.first().messageSuccessRate)
        assertTrue(points.last().characterErrorRate > points.first().characterErrorRate)
    }

    @Test
    fun `cli parses options and rejects unknown ones`() {
        val parsed = ChannelExperimentCli.parse(listOf("--seed", "7", "--trials", "10"))
        assertEquals(7L, parsed.seed)
        assertEquals(10, parsed.trialsPerPoint)
        assertThrows(IllegalArgumentException::class.java) { ChannelExperimentCli.parse(listOf("--nope", "1")) }
        assertThrows(IllegalArgumentException::class.java) { ChannelExperimentCli.parse(listOf("--seed")) }
    }

    @Test
    fun `csv has a header and one row per jitter level`() {
        val small = config.copy(trialsPerPoint = 5)
        val lines = ChannelExperimentCli.toCsv(small, ChannelExperiment(small).run()).trim().lines()
        assertEquals(ChannelExperimentCli.CSV_HEADER, lines.first())
        assertEquals(small.jitterFractions.size + 1, lines.size)
    }
}
