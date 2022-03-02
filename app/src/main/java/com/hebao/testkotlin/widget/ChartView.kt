package com.hebao.testkotlin.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.shrimp.base.utils.GenericTools
import java.util.*
import kotlin.math.max

/**
 * Created by chasing on 2022/3/1.
 * 图表--横屏模式     垂直方向为x轴，水平方向为y轴
 */
class ChartView(context: Context?, attr: AttributeSet?, defStyleAttr: Int) :
    View(context, attr, defStyleAttr) {
    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context?) : this(context, null)

    // region 提供外部设置属性
    private var isHorizontal = false
    private var offset = 100f
    private var arrowOffset = 12f
    private var textOffset = 10f
    private var axisTextSize = 15f
    private var lineTextSize = 10f
    // endregion

    private val axisPaint = Paint()
    private val axisTextPaint = Paint()
    private val linePaint = Paint()
    private val guidelinePaint = Paint()

    private val chartBeanList = ArrayList<ArrayList<ChartBean>>()
    private val chartBeanTypeList = ArrayList<String>()
    private var xAxisLength = 0f
    private var yAxisLength = 0f
    private var maxXPieceIndex = 0
    private var maxXPieceCount = 0 //坐标x轴分片总数量
    private var xPieceInterval = 0f //坐标x轴分片间距
    private var maxYPieceCount = 0 //坐标y轴分片总数量
    private var yPiece = 0 //坐标y轴分片单片大小
    private var yPieceInterval = 0f //坐标y轴分片间距

    private var maxY = 0 //数据中y轴方向包含的最大值
    private var axisPath: Path? = null //坐标轴路径
    private val pathMap = hashMapOf<String, Path?>()
    private val pointsMap = hashMapOf<String, MutableList<Float>?>()
    private val colorMap = hashMapOf<String, String>()

    init {
        axisPaint.color = Color.BLACK
        axisPaint.isAntiAlias = true
        axisPaint.style = Paint.Style.STROKE

        axisTextPaint.color = Color.BLACK
        axisTextPaint.isAntiAlias = true
        axisTextPaint.textAlign = Paint.Align.CENTER
        axisTextPaint.strokeWidth = 6f

        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 3f
        linePaint.textAlign = Paint.Align.CENTER

        guidelinePaint.color = Color.parseColor("#aeaeae")
        guidelinePaint.isAntiAlias = true
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isHorizontal) {
            xAxisLength = measuredWidth - offset * 2
            yAxisLength = measuredHeight - offset * 2
        } else {
            xAxisLength = measuredHeight - offset * 2
            yAxisLength = measuredWidth - offset * 2
        }
        refresh()
    }

    // region 外部调用方法
    fun addChartBeanList(chartBeanType: String, chartBeanList: ArrayList<ChartBean>) {
        this.chartBeanTypeList.add(chartBeanType)
        this.chartBeanList.add(chartBeanList)
        getMaxValue(chartBeanList)
        colorMap[chartBeanType] = getRandColor()
        refresh()
    }

    fun setHorizontal(horizontal: Boolean) {
        if (isHorizontal != horizontal) {
            isHorizontal = horizontal
            xAxisLength = yAxisLength.also { xAxisLength }
            refresh()
        }
    }

    fun setOffset(offset: Float) {
        if (this.offset != offset) {
            xAxisLength = xAxisLength + 2 * this.offset - 2 * offset
            yAxisLength = yAxisLength + 2 * this.offset - 2 * offset
            this.offset = offset
            refresh()
        }
    }

    fun setArrowOffset(arrowOffset: Float) {
        if (this.arrowOffset != arrowOffset) {
            this.arrowOffset = arrowOffset
            refresh()
        }
    }

    fun setTextOffset(textOffset: Float) {
        if (this.textOffset != textOffset) {
            this.textOffset = textOffset
            invalidate()
        }
    }

    fun setAxisTextSize(axisTextSize: Float) {
        if (this.axisTextSize != axisTextSize) {
            this.axisTextSize = axisTextSize
            invalidate()
        }
    }

    fun setLineTextSize(lineTextSize: Float) {
        if (this.lineTextSize != lineTextSize) {
            this.lineTextSize = lineTextSize
            invalidate()
        }
    }

    private fun refresh() {
        if (maxXPieceCount == 0 || xAxisLength == 0f) return
        pathMap.clear()
        pointsMap.clear()

        when {
            maxY <= 10 -> {
                yPiece = 1
            }
            maxY <= 100 -> {
                yPiece = 10
            }
            maxY <= 1000 -> {
                yPiece = 100
            }
            maxY <= 10000 -> {
                yPiece = 1000
            }
        }
        maxYPieceCount = maxY / yPiece + 1
        yPieceInterval = yAxisLength / maxYPieceCount
        xPieceInterval = xAxisLength / maxXPieceCount

        axisPath = Path()
        if (isHorizontal) {
            axisPath?.moveTo(offset, offset + yAxisLength + arrowOffset)
            axisPath?.lineTo(offset + xAxisLength + arrowOffset, offset + yAxisLength + arrowOffset)
            axisPath?.lineTo(offset + xAxisLength, offset + yAxisLength)
            axisPath?.moveTo(offset + xAxisLength + arrowOffset, offset + yAxisLength + arrowOffset)
            axisPath?.lineTo(offset + xAxisLength,
                offset + yAxisLength + 2 * arrowOffset)
            axisPath?.moveTo(offset, offset + yAxisLength + arrowOffset)
            axisPath?.lineTo(offset, offset)
            axisPath?.lineTo(offset - arrowOffset, offset + arrowOffset)
            axisPath?.moveTo(offset, offset)
            axisPath?.lineTo(offset + arrowOffset, offset + arrowOffset)
        } else {
            axisPath?.moveTo(offset + yAxisLength + arrowOffset, offset)
            axisPath?.lineTo(offset, offset)
            axisPath?.lineTo(offset, offset + xAxisLength + arrowOffset)
            axisPath?.lineTo(offset - arrowOffset, offset + xAxisLength)
            axisPath?.moveTo(offset, offset + xAxisLength + arrowOffset)
            axisPath?.lineTo(offset + arrowOffset, offset + xAxisLength)
            axisPath?.moveTo(offset + yAxisLength + arrowOffset, offset)
            axisPath?.lineTo(offset + yAxisLength, offset - arrowOffset)
            axisPath?.moveTo(offset + yAxisLength + arrowOffset, offset)
            axisPath?.lineTo(offset + yAxisLength, offset + arrowOffset)
        }

        for (index in chartBeanList.indices) {
            createPath(chartBeanTypeList[index], chartBeanList[index])
        }
        invalidate()
    }
    // endregion

    private fun getMaxValue(chartBeanList: List<ChartBean>) {
        if (chartBeanList.isEmpty()) return
        for (dailyInfo in chartBeanList) {
            maxY = max(maxY, dailyInfo.num)
        }
        if (chartBeanList.size > maxXPieceCount) {
            maxXPieceIndex = this.chartBeanList.size - 1
        }
        maxXPieceCount = max(maxXPieceCount, chartBeanList.size + 1)
    }

    private fun getHorizontalYAxis(chartBean: ChartBean): Float {
        val count = chartBean.num / yPiece
        val last = chartBean.num % yPiece
        return yAxisLength + offset  + arrowOffset - (count * yPieceInterval + last * yPieceInterval / yPiece)
    }

    private fun getVerticalXAxis(chartBean: ChartBean): Float {
        val count = chartBean.num / yPiece
        val last = chartBean.num % yPiece
        return count * yPieceInterval + last * yPieceInterval / yPiece + offset
    }

    private fun createPath(chartBeanType: String, chartBeanList: List<ChartBean>) {
        if (chartBeanList.isNotEmpty()) {
            var path = pathMap[chartBeanType]
            var points = pointsMap[chartBeanType]
            val xOffset = (maxXPieceCount - 1 - chartBeanList.size) * xPieceInterval
            var x: Float
            var y: Float
            for (index in chartBeanList.indices) {
                if (isHorizontal) {
                    x = offset + (index + 1) * xPieceInterval + xOffset
                    y = getHorizontalYAxis(chartBeanList[index])
                } else {
                    x = getVerticalXAxis(chartBeanList[index])
                    y = offset + (index + 1) * xPieceInterval + xOffset
                }
                if (path == null) {
                    path = Path()
                    pathMap[chartBeanType] = path
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                if (points == null) {
                    points = mutableListOf()
                    pointsMap[chartBeanType] = points
                }
                points.add(x)
                points.add(y)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (axisPath != null) {
            canvas.drawPath(axisPath!!, axisPaint)

            // 绘制x轴刻度
            axisTextPaint.textAlign = Paint.Align.CENTER
            axisTextPaint.textSize = GenericTools.dip2px(context, axisTextSize).toFloat()
            for (i in 0 until maxXPieceCount - 1) {
                if (isHorizontal) {
                    drawText(canvas,
                        chartBeanList[maxXPieceIndex][i].axisInfo,
                        offset + xPieceInterval * (i + 1),
                        offset + yAxisLength + axisTextPaint.textSize + textOffset,
                        axisTextPaint,
                        0f)
                    canvas.drawPoint(offset + xPieceInterval * (i + 1),
                        offset + yAxisLength + arrowOffset,
                        axisTextPaint)
                    canvas.drawLine(offset + xPieceInterval * (i + 1), offset,
                        offset + xPieceInterval * (i + 1), offset + yAxisLength, guidelinePaint)
                } else {
                    drawText(canvas,
                        chartBeanList[maxXPieceIndex][i].axisInfo,
                        offset - axisTextPaint.textSize - textOffset,
                        offset + xPieceInterval * (i + 1),
                        axisTextPaint,
                        90f)
                    canvas.drawPoint(offset, offset + xPieceInterval * (i + 1), axisTextPaint)
                    canvas.drawLine(offset, offset + xPieceInterval * (i + 1),
                        offset + yAxisLength, offset + xPieceInterval * (i + 1), guidelinePaint)
                }
            }

            // 绘制y轴刻度
            axisTextPaint.textAlign = Paint.Align.RIGHT
            for (i in 0..maxYPieceCount) {
                if (isHorizontal) {
                    drawText(canvas,
                        (i * yPiece).toString(),
                        offset - textOffset,
                        offset + yAxisLength + arrowOffset - i * yPieceInterval + axisTextPaint.textSize / 3,
                        axisTextPaint,
                        0f)
                    canvas.drawPoint(offset,
                        offset + yAxisLength + arrowOffset - i * yPieceInterval,
                        axisTextPaint)
                } else {
                    drawText(canvas,
                        (i * yPiece).toString(),
                        offset + i * yPieceInterval - axisTextPaint.textSize / 3,
                        offset - textOffset,
                        axisTextPaint,
                        90f)
                    canvas.drawPoint(offset + i * yPieceInterval, offset, axisTextPaint)
                }

                if (i != 0 && i != maxYPieceCount)
                    if (isHorizontal) {
                        canvas.drawLine(offset,
                            offset + arrowOffset + i * yPieceInterval,
                            offset + xAxisLength,
                            offset + arrowOffset + i * yPieceInterval,
                            guidelinePaint)
                    } else {
                        canvas.drawLine(offset + i * yPieceInterval, offset,
                            offset + i * yPieceInterval, offset + xAxisLength, guidelinePaint)
                    }
            }

            var path: Path?
            var points: MutableList<Float>?
            for ((offsetCount, chartBeanType) in chartBeanTypeList.withIndex()) {
                path = pathMap[chartBeanType]
                points = pointsMap[chartBeanType]

                linePaint.style = Paint.Style.FILL
                linePaint.color = Color.parseColor(colorMap[chartBeanType])
                linePaint.textSize = GenericTools.dip2px(context, lineTextSize).toFloat()
                if (isHorizontal) {
                    drawText(canvas,
                        chartBeanType,
                        xAxisLength,
                        offset + (linePaint.textSize + textOffset) * offsetCount,
                        linePaint,
                        0f)
                } else {
                    drawText(canvas,
                        chartBeanType,
                        yAxisLength + offset - (linePaint.textSize + textOffset) * offsetCount,
                        xAxisLength,
                        linePaint,
                        90f)
                }
                linePaint.style = Paint.Style.STROKE

                if (path != null) {
                    canvas.drawPath(path, linePaint)
                    points?.let {
                        canvas.drawPoints(it.toFloatArray(), axisTextPaint)
                        axisTextPaint.textSize =
                            GenericTools.dip2px(context, lineTextSize).toFloat()
                        for (index in it.indices step 2) {
                            drawText(canvas,
                                chartBeanList[offsetCount][index / 2].num.toString(),
                                it[index],
                                it[index + 1] - textOffset,
                                axisTextPaint,
                                if (isHorizontal) 0f else 90f)
                        }
                    }
                }
            }
        } else
            super.onDraw(canvas)
    }

    private fun drawText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: Paint,
        angle: Float,
    ) {
        if (angle != 0f)
            canvas.rotate(angle, x, y)
        canvas.drawText(text, x, y, paint)
        if (angle != 0f)
            canvas.rotate(-angle, x, y)
    }

    /**
     * 获取十六进制的颜色代码.例如  "#5A6677"
     * 分别取R、G、B的随机值，然后加起来即可
     *
     * @return String
     */
    private fun getRandColor(): String {
        var r: String
        var g: String
        var b: String
        val random = Random()
        r = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
        g = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
        b = Integer.toHexString(random.nextInt(256)).uppercase(Locale.getDefault())
        r = if (r.length == 1) "0$r" else r
        g = if (g.length == 1) "0$g" else g
        b = if (b.length == 1) "0$b" else b
        return "#$r$g$b"
    }
}