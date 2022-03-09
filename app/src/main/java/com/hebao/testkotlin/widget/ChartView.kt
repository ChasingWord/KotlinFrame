package com.hebao.testkotlin.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Scroller
import com.shrimp.base.utils.GenericTools
import java.util.*
import kotlin.math.max


/**
 * Created by chasing on 2022/3/1.
 * 图表--横屏模式     垂直方向为x轴，水平方向为y轴
 * 暂未仅支持Y轴值大于1的
 */
class ChartView(context: Context?, attr: AttributeSet?, defStyleAttr: Int) :
    View(context, attr, defStyleAttr), ScaleGestureDetector.OnScaleGestureListener,
    GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context?) : this(context, null)

    // region 提供外部设置属性
    // 屏幕方向是否为垂直方向
    var isScreenVertical = false
        set(value) {
            if (field != value) {
                field = value
                xAxisLength = yAxisLength.also { yAxisLength = xAxisLength }
                refresh()
            }
        }
    // 整体坐标轴偏移量
    var offset = 100f
        set(value) {
            if (field != value) {
                xAxisLength = xAxisLength + 2 * field - 2 * value
                yAxisLength = yAxisLength + 2 * field - 2 * value
                field = value
                refresh()
            }
        }
    // 坐标轴箭头偏移量
    var arrowOffset = 12f
        set(value) {
            if (field != value) {
                field = value
                refresh()
            }
        }
    // 文本与坐标轴偏移量
    var textOffset = 10f
        set(value) {
            if (field != value) {
                field = value
                refresh()
            }
        }
    // 坐标轴字体大小
    var axisTextSize = 15f
        set(value) {
            if (field != value) {
                field = value
                refresh()
            }
        }
    // 线上数据文本字体大小
    var lineTextSize = 10f
        set(value) {
            if (field != value) {
                field = value
                refresh()
            }
        }
    // 线对应的类型文本字体大小
    var lineTypeTextSize = 12f
        set(value) {
            if (field != value) {
                field = value
                refresh()
            }
        }
    // endregion

    private val axisPaint = Paint()
    private val axisTextPaint = Paint()
    private val linePaint = Paint()
    private val guidelinePaint = Paint()

    private var minTextSpace = 100f
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
    private val pathMap = hashMapOf<String, Path?>() //数据线
    private val pointsMap = hashMapOf<String, MutableList<Float>?>() //数据点
    private val colorMap = hashMapOf<String, String>() //每条数据线的颜色

    private val scaleGestureDetector: ScaleGestureDetector
    private val gestureDetector: GestureDetector
    private val scroller: Scroller
    private val minScale = 1f
    private val maxScale = 2f
    private var scale = 1f
    private var xOffset = 0f //x轴的偏移量
    private var yOffset = 0f //y轴的偏移量

    init {
        axisPaint.color = Color.BLACK
        axisPaint.isAntiAlias = true
        axisPaint.style = Paint.Style.STROKE

        axisTextPaint.color = Color.BLACK
        axisTextPaint.isAntiAlias = true
        axisTextPaint.textAlign = Paint.Align.CENTER
        axisTextPaint.strokeWidth = 6f
        axisTextPaint.textSize = GenericTools.dip2px(context, axisTextSize).toFloat()

        linePaint.isAntiAlias = true
        linePaint.style = Paint.Style.STROKE
        linePaint.strokeWidth = 3f
        linePaint.textAlign = Paint.Align.CENTER

        guidelinePaint.color = Color.parseColor("#aeaeae")
        guidelinePaint.isAntiAlias = true
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)

        scaleGestureDetector = ScaleGestureDetector(context, this)
        gestureDetector = GestureDetector(context, this)
        scroller = Scroller(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (isScreenVertical) {
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
        this.chartBeanList.add(ArrayList(chartBeanList))
        checkMaxValue(chartBeanList)
        colorMap[chartBeanType] = getRandColor()
        refresh()
    }

    private fun checkMaxValue(chartBeanList: List<ChartBean>) {
        if (chartBeanList.isEmpty()) return
        var maxXTextLength = ""
        for (dailyInfo in chartBeanList) {
            maxY = max(maxY, dailyInfo.num)
            maxXTextLength =
                if (dailyInfo.axisInfo.length > maxXTextLength.length) dailyInfo.axisInfo
                else maxXTextLength
        }
        if (chartBeanList.size > maxXPieceCount) {
            maxXPieceIndex = this.chartBeanList.size - 1
        }
        maxXPieceCount = max(maxXPieceCount, chartBeanList.size + 1)

        minTextSpace = axisTextPaint.measureText(maxXTextLength + "占")
        offset = axisTextPaint.measureText((maxY * 100).toString())
    }
    // endregion

    // 刷新全部（坐标轴、数据）
    private fun refresh() {
        if (maxXPieceCount == 0 || xAxisLength == 0f) return

        // region 坐标轴信息
        var digits = 0 //位数
        var tempMaxY = maxY
        while (tempMaxY > 1) {
            digits++
            tempMaxY /= 10
        }
        if (digits > 0) { // 位数大于0则表明maxY大于1
            yPiece = 1
            while (digits > 1) {
                yPiece *= 10
                digits--
            }
        }

        maxYPieceCount = (maxY / yPiece + 1)
        yPieceInterval = yAxisLength / maxYPieceCount
        xPieceInterval = xAxisLength / maxXPieceCount

        axisPath = Path()
        if (isScreenVertical) {
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
        // endregion

        refreshData()
    }

    // 刷新数据
    private fun refreshData() {
        if (maxXPieceCount == 0 || xAxisLength == 0f) return
        pathMap.clear()
        pointsMap.clear()
        for (index in chartBeanList.indices) {
            createPathAndPoints(chartBeanTypeList[index], chartBeanList[index])
        }
        invalidate()
    }

    // 创建数据线path及数据点point
    private fun createPathAndPoints(chartBeanType: String, chartBeanList: List<ChartBean>) {
        if (chartBeanList.isNotEmpty()) {
            var path = pathMap[chartBeanType]
            var points = pointsMap[chartBeanType]
            val xOffset = (maxXPieceCount - 1 - chartBeanList.size) * xPieceInterval * scale
            var x: Float
            var y: Float
            for (index in chartBeanList.indices) {
                if (isScreenVertical) {
                    x = offset + (index + 1) * xPieceInterval * scale + xOffset + this.xOffset
                    y = getVerticalYAxis(chartBeanList[index]) + this.yOffset
                } else {
                    x = getHorizontalXAxis(chartBeanList[index]) + this.yOffset
                    y = offset + (index + 1) * xPieceInterval * scale + xOffset + this.xOffset
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

    private fun getVerticalYAxis(chartBean: ChartBean): Float {
        val count = chartBean.num / yPiece
        val last = chartBean.num % yPiece
        return yAxisLength + offset + arrowOffset - (count * yPieceInterval + last * yPieceInterval / yPiece) * scale
    }

    private fun getHorizontalXAxis(chartBean: ChartBean): Float {
        val count = chartBean.num / yPiece
        val last = chartBean.num % yPiece
        return offset + (count * yPieceInterval + last * yPieceInterval / yPiece) * scale
    }

    override fun onDraw(canvas: Canvas) {
        if (axisPath != null) {
            // 绘制坐标轴
            canvas.drawPath(axisPath!!, axisPaint)

            // region 绘制x轴刻度
            axisTextPaint.textAlign = Paint.Align.CENTER
            axisTextPaint.textSize = GenericTools.dip2px(context, axisTextSize).toFloat()
            val stepCount: Int = when {
                xPieceInterval * scale <= minTextSpace -> (minTextSpace / (xPieceInterval * scale)).toInt() + 1
                else -> 1
            }
            for (i in 0 until maxXPieceCount - 1 step stepCount) {
                if (isScreenVertical) {
                    val x = offset + xPieceInterval * (i + 1) * scale + xOffset
                    if (x >= offset) {
                        drawText(canvas,
                            chartBeanList[maxXPieceIndex][i].axisInfo,
                            x,
                            offset + yAxisLength + axisTextPaint.textSize + textOffset,
                            axisTextPaint,
                            0f)
                        canvas.drawPoint(x,
                            offset + yAxisLength + arrowOffset,
                            axisTextPaint)
                        canvas.drawLine(x,
                            offset,
                            x,
                            offset + yAxisLength,
                            guidelinePaint)
                    }
                } else {
                    val y = offset + xPieceInterval * (i + 1) * scale + xOffset
                    if (y >= offset) {
                        drawText(canvas,
                            chartBeanList[maxXPieceIndex][i].axisInfo,
                            offset - axisTextPaint.textSize - textOffset,
                            y,
                            axisTextPaint,
                            90f)
                        canvas.drawPoint(offset,
                            y,
                            axisTextPaint)
                        canvas.drawLine(offset,
                            y,
                            offset + yAxisLength,
                            y,
                            guidelinePaint)
                    }
                }
            }
            // endregion

            // region 绘制y轴刻度
            axisTextPaint.textAlign = Paint.Align.RIGHT
            for (i in 0..maxYPieceCount) {
                if (isScreenVertical) {
                    val y =
                        offset + yAxisLength + arrowOffset - i * yPieceInterval * scale + yOffset
                    if (y <= offset + yAxisLength + arrowOffset) {
                        drawText(canvas,
                            (i * yPiece).toString(),
                            offset - textOffset,
                            y + axisTextPaint.textSize / 3,
                            axisTextPaint,
                            0f)
                        canvas.drawPoint(offset,
                            y,
                            axisTextPaint)
                    }
                } else {
                    val x = offset + i * yPieceInterval * scale + yOffset
                    if (x >= offset) {
                        drawText(canvas,
                            (i * yPiece).toString(),
                            x - axisTextPaint.textSize / 3,
                            offset - textOffset,
                            axisTextPaint,
                            90f)
                        canvas.drawPoint(x,
                            offset,
                            axisTextPaint)
                    }
                }

                if (i != 0 && i != maxYPieceCount)
                    if (isScreenVertical) {
                        canvas.drawLine(offset,
                            offset + yAxisLength + arrowOffset - i * yPieceInterval * scale + yOffset,
                            offset + xAxisLength,
                            offset + yAxisLength + arrowOffset - i * yPieceInterval * scale + yOffset,
                            guidelinePaint)
                    } else {
                        canvas.drawLine(offset + i * yPieceInterval * scale + yOffset,
                            offset,
                            offset + i * yPieceInterval * scale + yOffset,
                            offset + xAxisLength,
                            guidelinePaint)
                    }
            }
            // endregion

            // region 绘制数据点及数据线
            var path: Path?
            var points: MutableList<Float>?
            for ((offsetCount, chartBeanType) in chartBeanTypeList.withIndex()) {
                path = pathMap[chartBeanType]
                points = pointsMap[chartBeanType]

                // 绘制每条线对应的类型文本
                linePaint.style = Paint.Style.FILL
                linePaint.color = Color.parseColor(colorMap[chartBeanType])
                linePaint.textSize = GenericTools.dip2px(context, lineTypeTextSize).toFloat()
                if (isScreenVertical) {
                    drawText(canvas,
                        chartBeanType,
                        xAxisLength + offset,
                        offset + (linePaint.textSize + textOffset) * offsetCount,
                        linePaint,
                        0f)
                } else {
                    drawText(canvas,
                        chartBeanType,
                        yAxisLength + offset - (linePaint.textSize + textOffset) * offsetCount,
                        xAxisLength + offset,
                        linePaint,
                        90f)
                }

                if (path != null) {
                    linePaint.style = Paint.Style.STROKE
                    // 连线
                    canvas.drawPath(path, linePaint)
                    points?.let {
                        axisTextPaint.textAlign = Paint.Align.CENTER
                        // 画点
                        canvas.drawPoints(it.toFloatArray(), axisTextPaint)
                        axisTextPaint.textSize =
                            GenericTools.dip2px(context, lineTextSize).toFloat()
                        // 画字
                        for (index in it.indices step 2) {
                            drawText(canvas,
                                chartBeanList[offsetCount][index / 2].num.toString(),
                                it[index],
                                it[index + 1] - textOffset,
                                axisTextPaint,
                                if (isScreenVertical) 0f else 90f)
                        }
                    }
                }
            }
            // endregion
        } else
            super.onDraw(canvas)
    }

    // 绘制文本----改变字体旋转方向angle
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    private var isScaleGesture = false

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        isScaleGesture = true
        val preScale = scale
        scale *= detector.scaleFactor
        scale = when {
            scale <= minScale -> minScale
            scale >= maxScale -> maxScale
            else -> scale
        }
        if (preScale != scale)
            refreshData()
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean = true

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        isScaleGesture = false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        scroller.forceFinished(true)
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean = true

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {
        if (!isScaleGesture) {
            if (isScreenVertical) {
                xOffset -= distanceX
                yOffset -= distanceY
            } else {
                yOffset -= distanceX
                xOffset -= distanceY
            }
            checkOffset()
            refreshData()
        }
        return true
    }

    private fun checkOffset() {
        if (isScreenVertical) {
            if (xOffset >= 0) xOffset = 0f
            else {
                val scrollX = xAxisLength * (scale - 1)
                if (scrollX > 0) {
                    if (-xOffset >= scrollX)
                        xOffset = -scrollX
                } else
                    xOffset = 0f
            }

            if (yOffset <= 0) yOffset = 0f
            else {
                val scrollY = yAxisLength * (scale - 1)
                if (scrollY > 0) {
                    if (yOffset >= scrollY)
                        yOffset = scrollY
                } else
                    yOffset = 0f
            }
        } else {
            if (xOffset >= 0) xOffset = 0f
            else {
                val scrollX = xAxisLength * (scale - 1)
                if (scrollX > 0) {
                    if (-xOffset >= scrollX)
                        xOffset = -scrollX
                } else
                    xOffset = 0f
            }

            if (yOffset >= 0) yOffset = 0f
            else {
                val scrollY = yAxisLength * (scale - 1)
                if (scrollY > 0) {
                    if (-yOffset >= scrollY)
                        yOffset = -scrollY
                } else
                    yOffset = 0f
            }
        }
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        if (!isScaleGesture) {
            val scrollX = xAxisLength * (scale - 1)
            if (scrollX > 0) {
                if (isScreenVertical) {
                    val scrollY = yAxisLength * (scale - 1)
                    scroller.fling(0, 0, velocityX.toInt(), velocityY.toInt(),
                        xOffset.toInt(), (scrollX + xOffset).toInt(),
                        (-yOffset).toInt(), (scrollY - yOffset).toInt())
                } else {
                    val scrollY = yAxisLength * (scale - 1)
                    scroller.fling(0, 0, velocityX.toInt(), velocityY.toInt(),
                        yOffset.toInt(), (scrollY + yOffset).toInt(),
                        xOffset.toInt(), (scrollX + xOffset).toInt())
                }
            }
        }
        return true
    }

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean = true

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        scale = when {
            scale == maxScale -> {
                xOffset = 0f
                yOffset = 0f
                1f
            }
            scale >= maxScale / 2 -> maxScale
            else -> scale * 2
        }
        refreshData()
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean = true

    private var preCurrX = 0
    private var preCurrY = 0
    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            if (isScreenVertical) {
                xOffset += scroller.currX - preCurrX
                yOffset += scroller.currY - preCurrY
            } else {
                yOffset += scroller.currX - preCurrX
                xOffset += scroller.currY - preCurrY
            }
            checkOffset()
            refreshData()
            preCurrX = scroller.currX
            preCurrY = scroller.currY
        } else {
            preCurrX = 0
            preCurrY = 0
        }
    }
}