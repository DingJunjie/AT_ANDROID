package com.bitat.utils

import kotlin.math.abs

data class ImageParam(val width: Int, val height: Int)

object ImageUtils {

    private val regex = """x_(\d+)&y_(\d+)""".toRegex()

    fun getSizeFromUrl(url: String): ImageParam {

        val matchResult = regex.find(url)

        if (matchResult != null) {
            val (x, y) = matchResult.destructured
            return ImageParam(width = x.toInt(), height = y.toInt())
        } else {
            println("No match found")
            return ImageParam(0, 0)
        }
    }

    fun getResourceHeight(
        url: String,
        fixedWidth: Double,
    ): Double {
        // type 在博文专辑中使用时，调整长图比例
        val matchResult = regex.find(url)

        if (matchResult != null) {
            val (w, h) = matchResult.destructured
            val x = w.toDouble();
            val y = h.toDouble();

            if (x > y) {
                return if (abs(y / x - 9 / 16) < 0.1 || y / x < 9 / 16) {
                    3 / 4 * fixedWidth;
                } else if (abs(y / x - 0.75) < 0.1) {
                    3 / 4 * fixedWidth;
                } else {
                    fixedWidth;
                }
            } else if (x == y) {
                // 正方形
                return fixedWidth;
            } else {
                // 竖图
                return if (abs(y / x - 16 / 9) < 0.22 || y / x > 16 / 9) {
                    16 / 9 * fixedWidth;
                } else if (abs(y / x - 4 / 3) < 0.1) {
                    4 / 3 * fixedWidth;
                } else {
                    4 / 3 * fixedWidth;
                }
            }
        } else {
            return 0.0;
        }
    }

    fun getResourceImageHeight(
        size: ImageParam,
        fixedWidth: Int,
    ): Int {
        if (size.height == 0 || size.width == 0 || fixedWidth == 0) {
            return (ScreenUtils.screenWidth * 0.88 * 1.25).toInt()

        }
        val total = fixedWidth * size.height
        val fixedHeight = total / size.width
        return fixedHeight

    }

    /***
     * if (x > y) {
     *         if ((y / x - 9 / 16).abs() < 0.1 || y / x < 9 / 16) {
     *           return 3 / 4 * fixedWidth;
     *         } else if ((y / x - 0.75).abs() < 0.1) {
     *           return 3 / 4 * fixedWidth;
     *         } else {
     *           return fixedWidth;
     *         }
     *       } else if (x == y) {
     *         // 正方形
     *         return fixedWidth;
     *       } else {
     *         // 竖图
     *         if ((y / x - 16 / 9).abs() < 0.22 || y / x > 16 / 9) {
     *           return 16 / 9 * fixedWidth;
     *         } else if ((y / x - 4 / 3).abs() < 0.1) {
     *           return 4 / 3 * fixedWidth;
     *         } else {
     *           return 4 / 3 * fixedWidth;
     *         }
     *       }
     */

    fun getResourceHeight(
        size: ImageParam,
        fixedWidth: Int,
    ): Int {
        val (x, y) = size;
        if (x == 0 || y == 0 || fixedWidth == 0) {
            return (ScreenUtils.screenWidth * 0.88 * 1.25).toInt()
        }

//        val total = fixedWidth * size.height
//        val fixedHeight = total / size.width

        var height = y;

        if (x > y) {
            height = if (abs(y / x - 9 / 16) < 0.1 || y / x < 9 / 16) {
                (0.75 * fixedWidth).toInt();
            } else if (abs(y / x - 0.75) < 0.1) {
                (0.75 * fixedWidth).toInt();
            } else {
                fixedWidth;
            }
        } else if (x == y) {
            // 正方形
            height = fixedWidth;
        } else {
            // 竖图
            height = if (abs(y / x - 16 / 9) < 0.22 || y / x > 16 / 9) {
                (16f / 9f * fixedWidth).toInt();
            } else if (abs(y / x - 4 / 3) < 0.1) {
                (4 / 3 * fixedWidth);
            } else {
                (4 / 3 * fixedWidth);
            }
        }
        return height
    }
}