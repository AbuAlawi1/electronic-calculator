package com.example.engine

import kotlin.math.*

object MathToolsEngine {

    // 1. Statistics
    data class StatsResult(
        val count: Int,
        val sum: Double,
        val mean: Double,
        val median: Double,
        val mode: List<Double>,
        val min: Double,
        val max: Double,
        val variance: Double,
        val stdDev: Double
    )

    fun calculateStats(numbers: List<Double>): StatsResult? {
        if (numbers.isEmpty()) return null
        val count = numbers.size
        val sum = numbers.sum()
        val mean = sum / count
        val sorted = numbers.sorted()
        val median = if (count % 2 == 1) {
            sorted[count / 2]
        } else {
            (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0
        }
        val freqMap = numbers.groupingBy { it }.eachCount()
        val maxFreq = freqMap.values.maxOrNull() ?: 1
        val mode = if (maxFreq > 1) {
            freqMap.filter { it.value == maxFreq }.keys.toList()
        } else {
            emptyList()
        }
        val variance = numbers.map { (it - mean).pow(2) }.sum() / count
        val stdDev = sqrt(variance)

        return StatsResult(
            count = count,
            sum = sum,
            mean = mean,
            median = median,
            mode = mode,
            min = sorted.first(),
            max = sorted.last(),
            variance = variance,
            stdDev = stdDev
        )
    }

    // 2. Ratio & Proportion: A/B = C/D -> solve missing
    fun solveProportion(a: Double?, b: Double?, c: Double?, d: Double?): Double? {
        return when {
            a == null && b != null && c != null && d != null && d != 0.0 -> (b * c) / d
            b == null && a != null && c != null && d != null && c != 0.0 -> (a * d) / c
            c == null && a != null && b != null && d != null && b != 0.0 -> (a * d) / b
            d == null && a != null && b != null && c != null && a != 0.0 -> (b * c) / a
            else -> null
        }
    }

    // 3. LCM & GCD
    fun gcd(a: Long, b: Long): Long {
        var x = abs(a)
        var y = abs(b)
        while (y != 0L) {
            val temp = y
            y = x % y
            x = temp
        }
        return x
    }

    fun lcm(a: Long, b: Long): Long {
        if (a == 0L || b == 0L) return 0L
        return abs(a * b) / gcd(a, b)
    }

    fun gcdMultiple(numbers: List<Long>): Long {
        if (numbers.isEmpty()) return 0L
        return numbers.reduce { acc, n -> gcd(acc, n) }
    }

    fun lcmMultiple(numbers: List<Long>): Long {
        if (numbers.isEmpty()) return 0L
        return numbers.reduce { acc, n -> lcm(acc, n) }
    }

    // 4. Prime Numbers & Factorization
    fun isPrime(n: Long): Boolean {
        if (n <= 1L) return false
        if (n <= 3L) return true
        if (n % 2L == 0L || n % 3L == 0L) return false
        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2L) == 0L) return false
            i += 6L
        }
        return true
    }

    fun nextPrime(n: Long): Long {
        var p = if (n < 2) 2L else n + 1
        while (!isPrime(p)) {
            p++
        }
        return p
    }

    fun prevPrime(n: Long): Long? {
        if (n <= 2) return null
        var p = n - 1
        while (p >= 2 && !isPrime(p)) {
            p--
        }
        return if (p >= 2) p else null
    }

    fun getFactors(n: Long): List<Long> {
        if (n <= 0) return emptyList()
        val factors = mutableListOf<Long>()
        var i = 1L
        while (i * i <= n) {
            if (n % i == 0L) {
                factors.add(i)
                if (i * i != n) {
                    factors.add(n / i)
                }
            }
            i++
        }
        return factors.sorted()
    }

    fun primeFactorization(n: Long): Map<Long, Int> {
        if (n <= 1) return emptyMap()
        var temp = n
        val factors = mutableMapOf<Long, Int>()
        var d = 2L
        while (d * d <= temp) {
            while (temp % d == 0L) {
                factors[d] = (factors[d] ?: 0) + 1
                temp /= d
            }
            d++
        }
        if (temp > 1) {
            factors[temp] = (factors[temp] ?: 0) + 1
        }
        return factors
    }

    // 5. Permutations & Combinations
    fun permutations(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0L
        var res = 1L
        for (i in (n - r + 1)..n) {
            res *= i
        }
        return res
    }

    fun combinations(n: Long, r: Long): Long {
        if (n < 0 || r < 0 || r > n) return 0L
        val k = if (r > n - r) n - r else r
        var res = 1L
        for (i in 1..k) {
            res = res * (n - i + 1) / i
        }
        return res
    }

    // 6. Right Triangle Solver
    data class TriangleResult(
        val a: Double,
        val b: Double,
        val c: Double,
        val angleADeg: Double,
        val angleBDeg: Double,
        val area: Double,
        val perimeter: Double
    )

    fun solveRightTriangleBySides(a: Double?, b: Double?, c: Double?): TriangleResult? {
        val sideA: Double
        val sideB: Double
        val sideC: Double
        if (a != null && b != null) {
            sideA = a
            sideB = b
            sideC = sqrt(a * a + b * b)
        } else if (a != null && c != null && c > a) {
            sideA = a
            sideC = c
            sideB = sqrt(c * c - a * a)
        } else if (b != null && c != null && c > b) {
            sideB = b
            sideC = c
            sideA = sqrt(c * c - b * b)
        } else {
            return null
        }
        val angleA = Math.toDegrees(asin(sideA / sideC))
        val angleB = 90.0 - angleA
        val area = 0.5 * sideA * sideB
        val perimeter = sideA + sideB + sideC

        return TriangleResult(sideA, sideB, sideC, angleA, angleB, area, perimeter)
    }

    // 7. Geometry Shapes
    data class GeometryResult(
        val perimeter: Double?,
        val area: Double?,
        val surfaceArea: Double?,
        val volume: Double?
    )

    fun calcSquare(side: Double) = GeometryResult(
        perimeter = 4 * side,
        area = side * side,
        surfaceArea = null,
        volume = null
    )

    fun calcRectangle(length: Double, width: Double) = GeometryResult(
        perimeter = 2 * (length + width),
        area = length * width,
        surfaceArea = null,
        volume = null
    )

    fun calcCircle(radius: Double) = GeometryResult(
        perimeter = 2 * Math.PI * radius,
        area = Math.PI * radius * radius,
        surfaceArea = null,
        volume = null
    )

    fun calcTriangle(base: Double, height: Double, sideA: Double, sideC: Double) = GeometryResult(
        perimeter = base + sideA + sideC,
        area = 0.5 * base * height,
        surfaceArea = null,
        volume = null
    )

    fun calcCylinder(radius: Double, height: Double) = GeometryResult(
        perimeter = null,
        area = null,
        surfaceArea = 2 * Math.PI * radius * (radius + height),
        volume = Math.PI * radius * radius * height
    )

    fun calcSphere(radius: Double) = GeometryResult(
        perimeter = null,
        area = null,
        surfaceArea = 4 * Math.PI * radius * radius,
        volume = (4.0 / 3.0) * Math.PI * radius.pow(3)
    )

    fun calcCone(radius: Double, height: Double): GeometryResult {
        val slant = sqrt(radius * radius + height * height)
        return GeometryResult(
            perimeter = null,
            area = null,
            surfaceArea = Math.PI * radius * (radius + slant),
            volume = (1.0 / 3.0) * Math.PI * radius * radius * height
        )
    }

    fun calcCube(side: Double) = GeometryResult(
        perimeter = null,
        area = null,
        surfaceArea = 6 * side * side,
        volume = side.pow(3)
    )

    // 8. Fractions
    data class FractionResult(
        val numerator: Long,
        val denominator: Long,
        val wholeNumber: Long?,
        val remainderNum: Long?,
        val decimal: Double
    )

    fun calculateFractions(
        num1: Long, den1: Long,
        op: String,
        num2: Long, den2: Long
    ): FractionResult? {
        if (den1 == 0L || den2 == 0L) return null

        var resNum: Long
        var resDen: Long

        when (op) {
            "+" -> {
                resNum = num1 * den2 + num2 * den1
                resDen = den1 * den2
            }
            "-" -> {
                resNum = num1 * den2 - num2 * den1
                resDen = den1 * den2
            }
            "×", "*" -> {
                resNum = num1 * num2
                resDen = den1 * den2
            }
            "÷", "/" -> {
                if (num2 == 0L) return null
                resNum = num1 * den2
                resDen = den1 * num2
            }
            else -> return null
        }

        if (resDen < 0) {
            resNum = -resNum
            resDen = -resDen
        }

        val g = gcd(abs(resNum), abs(resDen))
        val simpNum = if (g != 0L) resNum / g else resNum
        val simpDen = if (g != 0L) resDen / g else resDen

        val whole = if (abs(simpNum) >= simpDen) simpNum / simpDen else null
        val rem = if (whole != null && abs(simpNum) % simpDen != 0L) abs(simpNum) % simpDen else null

        val decimal = simpNum.toDouble() / simpDen.toDouble()

        return FractionResult(
            numerator = simpNum,
            denominator = simpDen,
            wholeNumber = whole,
            remainderNum = rem,
            decimal = decimal
        )
    }
}
