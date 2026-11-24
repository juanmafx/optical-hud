package com.juanmafx.opticalhud.core.channel

class SeededRandom(seed: Long) {
    private var state = seed

    fun nextLong(): Long {
        state += GOLDEN_GAMMA
        var mixed = state
        mixed = (mixed xor (mixed ushr 30)) * MIX_MULTIPLIER_A
        mixed = (mixed xor (mixed ushr 27)) * MIX_MULTIPLIER_B
        return mixed xor (mixed ushr 31)
    }

    fun nextDouble(): Double = (nextLong() ushr 11) * DOUBLE_UNIT

    fun nextInt(boundExclusive: Int): Int {
        require(boundExclusive > 0) { "boundExclusive must be positive" }
        val bound = boundExclusive.toLong()
        val limit = Long.MAX_VALUE - Long.MAX_VALUE % bound
        while (true) {
            val candidate = nextLong() ushr 1
            if (candidate < limit) return (candidate % bound).toInt()
        }
    }

    fun nextGaussian(): Double {
        val radius = StrictMath.sqrt(-2.0 * StrictMath.log(1.0 - nextDouble()))
        return radius * StrictMath.cos(2.0 * StrictMath.PI * nextDouble())
    }

    private companion object {
        const val GOLDEN_GAMMA = -0x61c8864680b583ebL
        const val MIX_MULTIPLIER_A = -0x40a7b892e31b1a47L
        const val MIX_MULTIPLIER_B = -0x6b2fb644ecceee15L
        const val DOUBLE_UNIT = 1.0 / (1L shl 53)
    }
}
