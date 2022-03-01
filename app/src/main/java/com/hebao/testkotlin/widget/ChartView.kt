package com.hebao.testkotlin.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.shrimp.base.utils.GenericTools
import kotlin.math.max

/**
 * Created by chasing on 2022/3/1.
 * 图表--横屏模式     垂直方向为x轴，水平方向为y轴
 */
class ChartView(context: Context?, attr: AttributeSet?, defStyleAttr: Int) :
    View(context, attr, defStyleAttr) {
    constructor(context: Context?, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context?) : this(context, null)

    private val coordinatePaint = Paint()
    private val coordinateTextPaint = Paint()
    private val androidPaint = Paint()
    private val iosPaint = Paint()
    private val guidelinePaint = Paint()

    private val androidDailyInfoList = ArrayList<DailyInfo>()
    private val iosDailyInfoList = ArrayList<DailyInfo>()

    private var totalHeight = 0f
    private var totalWidth = 0f
    private val offset = 100f
    private var maxXPieceCount = 0 //坐标x轴分片总数量
    private var xPieceInterval = 0f //坐标x轴分片间距
    private var maxYPieceCount = 0 //坐标y轴分片总数量
    private var yPiece = 0 //坐标y轴分片单片大小
    private var yPieceInterval = 0f //坐标y轴分片间距

    private var maxY = 0 //数据中y轴方向包含的最大值
    private var coordinatePath: Path? = null
    private var androidPoints = mutableListOf<Float>()
    private var androidNumPath: Path? = null
    private var iosPoints = ArrayList<Float>()
    private var iosNumPath: Path? = null

    init {
        coordinatePaint.color = Color.BLACK
        coordinatePaint.isAntiAlias = true
        coordinatePaint.style = Paint.Style.STROKE

        coordinateTextPaint.color = Color.BLACK
        coordinateTextPaint.isAntiAlias = true
        coordinateTextPaint.textAlign = Paint.Align.CENTER
        coordinateTextPaint.textSize = GenericTools.dip2px(getContext(), 15f).toFloat()
        coordinateTextPaint.strokeWidth = 6f

        androidPaint.color = Color.parseColor("#3498fe")
        androidPaint.isAntiAlias = true
        androidPaint.textAlign = Paint.Align.CENTER
        androidPaint.textSize = GenericTools.dip2px(getContext(), 15f).toFloat()
        androidPaint.style = Paint.Style.STROKE

        iosPaint.color = Color.parseColor("#fed851")
        iosPaint.isAntiAlias = true
        iosPaint.style = Paint.Style.STROKE
        iosPaint.textAlign = Paint.Align.CENTER
        iosPaint.textSize = GenericTools.dip2px(getContext(), 15f).toFloat()

        guidelinePaint.color = Color.parseColor("#aeaeae")
        guidelinePaint.isAntiAlias = true
        guidelinePaint.style = Paint.Style.STROKE
        guidelinePaint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        totalHeight = measuredHeight - offset * 2
        totalWidth = measuredWidth - offset * 2
        refresh()
    }

    fun setAndroidDailyInfo(dailyInfoList: List<DailyInfo>) {
        androidDailyInfoList.addAll(dailyInfoList)
        getMaxValue(dailyInfoList)
    }

    fun setIOSDailyInfo(dailyInfoList: List<DailyInfo>) {
        iosDailyInfoList.addAll(dailyInfoList)
        getMaxValue(dailyInfoList)
    }

    private fun getMaxValue(dailyInfoList: List<DailyInfo>) {
        if (dailyInfoList.isEmpty()) return
        for (dailyInfo in dailyInfoList) {
            maxY = max(maxY, dailyInfo.num)
        }
        maxXPieceCount = max(maxXPieceCount, dailyInfoList.size + 1)
    }

    fun refresh() {
        if (maxXPieceCount == 0 || totalHeight == 0f || maxYPieceCount > 0) return

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
        yPieceInterval = totalWidth / maxYPieceCount
        xPieceInterval = totalHeight / maxXPieceCount

        coordinatePath = Path()
        coordinatePath?.moveTo(offset + totalWidth + 12, offset)
        coordinatePath?.lineTo(offset, offset)
        coordinatePath?.lineTo(offset, offset + totalHeight + 12)
        coordinatePath?.lineTo(offset - 12, offset + totalHeight)
        coordinatePath?.moveTo(offset, offset + totalHeight + 12)
        coordinatePath?.lineTo(offset + 12, offset + totalHeight)
        coordinatePath?.moveTo(offset + totalWidth + 12, offset)
        coordinatePath?.lineTo(offset + totalWidth, offset - 12)
        coordinatePath?.moveTo(offset + totalWidth + 12, offset)
        coordinatePath?.lineTo(offset + totalWidth, offset + 12)

        createPath(androidDailyInfoList, true)
        createPath(iosDailyInfoList, false)

        invalidate()
    }

    private fun getDailyX(dailyInfo: DailyInfo): Float {
        val count = dailyInfo.num / yPiece
        val last = dailyInfo.num % yPiece
        return count * yPieceInterval + last * yPieceInterval / yPiece + offset
    }

    private fun createPath(dailyInfoList: List<DailyInfo>, isAndroid: Boolean) {
        if (dailyInfoList.isNotEmpty()) {
            var path = if (isAndroid) androidNumPath else iosNumPath
            val points = if (isAndroid) androidPoints else iosPoints
            val xOffset = (maxXPieceCount - 1 - dailyInfoList.size) * xPieceInterval
            var x: Float
            var y: Float
            for (index in dailyInfoList.indices) {
                x = getDailyX(dailyInfoList[index])
                y = offset + (index + 1) * xPieceInterval + xOffset
                if (path == null) {
                    path = Path()
                    if (isAndroid)
                        androidNumPath = path
                    else
                        iosNumPath = path
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                points.add(x)
                points.add(y)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (coordinatePath != null) {
            canvas.drawPath(coordinatePath!!, coordinatePaint)

            // 绘制y轴刻度
            coordinateTextPaint.textAlign = Paint.Align.RIGHT
            for (i in 0..maxYPieceCount) {
                drawText(canvas,
                    (i * yPiece).toString(),
                    offset - 10 + i * yPieceInterval,
                    offset - 10,
                    coordinateTextPaint,
                    90f)
                canvas.drawPoint(offset + i * yPieceInterval, offset, coordinateTextPaint)

                if (i != 0 && i != maxYPieceCount)
                    canvas.drawLine(offset + i * yPieceInterval, offset,
                        offset + i * yPieceInterval, offset + totalHeight, guidelinePaint)
            }

            // 绘制x轴刻度
            coordinateTextPaint.textAlign = Paint.Align.CENTER
            coordinateTextPaint.textSize = GenericTools.dip2px(context, 15f).toFloat()
            for (i in 0 until maxXPieceCount - 1) {
                drawText(canvas,
                    androidDailyInfoList[i].date,
                    offset - coordinateTextPaint.textSize / 2 - 20,
                    offset + xPieceInterval * (i + 1),
                    coordinateTextPaint,
                    90f)
                canvas.drawPoint(offset, offset + xPieceInterval * (i + 1), coordinateTextPaint)

                canvas.drawLine(offset, offset + xPieceInterval * (i + 1),
                    offset + totalWidth, offset + xPieceInterval * (i + 1), guidelinePaint)
            }

            // 绘制android点
            androidPaint.style = Paint.Style.FILL
            drawText(canvas,
                "Android",
                totalWidth,
                totalHeight,
                androidPaint,
                90f)
            androidPaint.style = Paint.Style.STROKE
            if (androidNumPath != null) {
                canvas.drawPath(androidNumPath!!, androidPaint)
                canvas.drawPoints(androidPoints.toFloatArray(), coordinateTextPaint)

                coordinateTextPaint.textSize = GenericTools.dip2px(context, 10f).toFloat()
                for (index in androidPoints.indices step 2) {
                    drawText(canvas,
                        androidDailyInfoList[index / 2].num.toString(),
                        androidPoints[index] + 12,
                        androidPoints[index + 1],
                        coordinateTextPaint,
                        90f)
                }
            }

            // 绘制iOS点
            iosPaint.style = Paint.Style.FILL
            drawText(canvas,
                "iOS",
                totalWidth - androidPaint.textSize - 30,
                totalHeight,
                iosPaint,
                90f)
            iosPaint.style = Paint.Style.STROKE
            if (iosNumPath != null) {
                canvas.drawPath(iosNumPath!!, iosPaint)
                canvas.drawPoints(iosPoints.toFloatArray(), coordinateTextPaint)

                coordinateTextPaint.textSize = GenericTools.dip2px(context, 10f).toFloat()
                for (index in iosPoints.indices step 2) {
                    drawText(canvas,
                        iosDailyInfoList[index / 2].num.toString(),
                        iosPoints[index] + 12,
                        iosPoints[index + 1],
                        coordinateTextPaint,
                        90f)
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
}