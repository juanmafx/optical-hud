package com.juanmafx.opticalhud.core.support

import com.juanmafx.opticalhud.core.morse.MorseAlphabet
import io.kotest.common.ExperimentalKotest
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string

const val PROPERTY_SEED = 20260317L

@OptIn(ExperimentalKotest::class)
fun propertyConfig(iterations: Int = 500) = PropTestConfig(seed = PROPERTY_SEED, iterations = iterations)

val canonicalWord: Arb<String> = Arb.list(Arb.element(MorseAlphabet.codes.keys.toList()), 1..8)
    .map { it.joinToString("") }

val canonicalText: Arb<String> = Arb.list(canonicalWord, 1..6).map { it.joinToString(" ") }

val arbitraryText: Arb<String> = Arb.string(0..64)
