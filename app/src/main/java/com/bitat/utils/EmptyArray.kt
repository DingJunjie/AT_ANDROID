package com.bitat.utils

@OptIn(ExperimentalUnsignedTypes::class)
object EmptyArray {
    val byte = byteArrayOf()
    val short = shortArrayOf()
    val int = intArrayOf()
    val long = longArrayOf()

    val ubyte = ubyteArrayOf()
    val ushort = ushortArrayOf()
    val uint = uintArrayOf()
    val ulong = ulongArrayOf()
    val bool = booleanArrayOf()

    val float = floatArrayOf()
    val double = doubleArrayOf()

}

