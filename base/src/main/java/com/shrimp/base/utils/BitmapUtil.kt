package com.shrimp.base.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.View
import androidx.exifinterface.media.ExifInterface
import java.io.*
import kotlin.math.sqrt

/**
 * Created by chasing on 2021/10/22.
 */
object BitmapUtil {
    /**
     * 社区中的图片压缩处理逻辑
     * 压缩大小：
     * a、社区中上传的图片宽*高不超过700000平方像素（即按正方形的图片约为836*836px）
     * a1、如果超过了70W平方像素，则等比例压缩到70w平方像素;
     * a2、如果没有超过，则按原来是多少就设置成多少.
     * b、图片大小压缩后一般应该为50k左右，注意应该最大不要超过80k[此规范可以不理会，大小可以多一点。因为后台加水印的时候会再次压缩，后台现在规定是150kb]。
     * c、社区中的图片精度控制为70.由于不对大小进行控制。此精度是合理的。[在FileUtil.saveAsCompressBitmap()方法进行了压缩操作]
     */
    private const val size = 700000 //图片宽*高不超过70w平方像素

    //获取图片尺寸,0为宽，1为高
    fun getPhotoSize(context: Context, path: String): IntArray {
        val size = IntArray(2)
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            options.inPreferredConfig = Bitmap.Config.ALPHA_8
            val bitmap =
                BitmapFactory.decodeStream(FileUtil.getInputStream(context, path), null, options)
            size[0] = options.outWidth
            size[1] = options.outHeight
            bitmap?.recycle()
        } catch (ignored: Exception) {
        }
        return size
    }

    //获取规定的宽度按比例缩放的Bitmap，可能会有几像素的偏差
    fun getBitmapWithSize(context: Context, path: String, w: Int): Bitmap? {
        val inputStream: InputStream = FileUtil.getInputStream(context, path) ?: return null
        val bitmapSize = getPhotoSize(context, path)
        var bitmap: Bitmap? = null
        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(inputStream)
            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inDensity = 1000
            options.inTargetDensity = (w / bitmapSize[0].toFloat() * options.inDensity).toInt()
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            bitmap = BitmapFactory.decodeStream(`in`, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bitmap
    }

    //得到Bitmap，宽高积的最大像素不超过size（70w像素）
    @Throws(IOException::class)
    fun getBitmapInMaxSize(path: String): Bitmap? {
        //取得图片的流
        var `in` = BufferedInputStream(FileInputStream(File(path)))
        val options = BitmapFactory.Options()
        //这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
        options.inJustDecodeBounds = true
        options.inPreferredConfig = Bitmap.Config.RGB_565
        //通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
        BitmapFactory.decodeStream(`in`, null, options)
        `in`.close()
        //生成压缩的图片
        `in` = BufferedInputStream(FileInputStream(File(path)))
        //这里之前设置为了true，所以要改为false，否则就创建不出图片
        options.inJustDecodeBounds = false
        if (options.outHeight * options.outWidth > size) {
            //创建值
            val d = size.toDouble() / (options.outHeight * options.outWidth).toDouble()
            options.inScaled = true
            options.inDensity = 1000
            options.inTargetDensity = (sqrt(d) * 1000).toInt()
            // will load & resize the image to be 1/inSampleSize dimensions
        }
        val bitmap: Bitmap? = BitmapFactory.decodeStream(`in`, null, options)
        `in`.close()
        return bitmap
    }

    //获取规定比例的Bitmap
    fun getBitmapWithScale(path: String, scale: Int): Bitmap? {
        var bitmap: Bitmap? = null
        var `in`: BufferedInputStream? = null
        try {
            `in` = BufferedInputStream(FileInputStream(File(path)))
            val options = BitmapFactory.Options()
            options.inSampleSize = scale
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            bitmap = BitmapFactory.decodeStream(`in`, null, options)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                `in`?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return bitmap
    }

    // 根据原图尺寸及需要的尺寸计算缩放比例
    fun getCalculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 压缩图片并转换成输出流
     *
     * @param image 要压缩的图片
     * @param size  图片压缩尺寸，单位 kb
     */
    fun getByteArrayOutputStream(image: Bitmap, size: Int): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到outputStream中
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        var options = 100
        // 循环判断如果压缩后图片是否大于 size kb,大于继续压缩
        while (outputStream.toByteArray().size / 1024 > size && options > 10) {
            // 重置outputStream即清空outputStream
            outputStream.reset()
            // 每次都减少10
            options -= 10
            // 这里压缩options%，把压缩后的数据存放到outputStream中
            image.compress(Bitmap.CompressFormat.JPEG, options, outputStream)
        }
        return outputStream
    }

    // 将图片字节数组转换成缩放的bitmap
    fun getCompressBitmap(
        context: Context,
        bytes: ByteArray,
        allowWidth: Int,
        allowHeight: Int
    ): Bitmap? {
        //循环压缩直到其大小小于32kb
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true //设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        opts.inSampleSize = getCalculateInSampleSize(opts, allowWidth, allowHeight)
        opts.inScaled = true
        val xSScale = opts.outWidth.toDouble() / allowWidth.toDouble()
        val ySScale = opts.outHeight.toDouble() / allowHeight.toDouble()
        val startScale = xSScale.coerceAtLeast(ySScale)
        val targetDensity = context.resources.displayMetrics.densityDpi
        opts.inDensity = (targetDensity * startScale).toInt()
        opts.inTargetDensity = targetDensity
        opts.inJustDecodeBounds = false //这里之前设置为了true，所以要改为false，否则就创建不出图片
        //Log.e("测试", "" + newBytes.length);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opts)
    }

    // 从resource资源获取缩放的bitmap
    fun getCompressBitmapFromResource(
        resources: Resources,
        resId: Int,
        allowWidth: Int,
        allowHeight: Int
    ): Bitmap? {
        //循环压缩直到其大小小于32kb
        val opts = BitmapFactory.Options()
        opts.inJustDecodeBounds = true //设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeResource(resources, resId)
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        opts.inSampleSize = getCalculateInSampleSize(opts, allowWidth, allowHeight)
        opts.inScaled = true
        val xSScale = opts.outWidth.toDouble() / allowWidth.toDouble()
        val ySScale = opts.outHeight.toDouble() / allowHeight.toDouble()
        val startScale = xSScale.coerceAtLeast(ySScale)
        val targetDensity = resources.displayMetrics.densityDpi
        opts.inDensity = (targetDensity * startScale).toInt()
        opts.inTargetDensity = targetDensity
        opts.inJustDecodeBounds = false //这里之前设置为了true，所以要改为false，否则就创建不出图片
        //Log.e("测试", "" + newBytes.length);
        return BitmapFactory.decodeResource(resources, resId, opts)
    }

    // 对Bitmap进行缩放
    fun getZoomImage(bitmap: Bitmap?, maxSize: Double): Bitmap? {
        var bitmap = bitmap
        if (null == bitmap) {
            return null
        }
        if (bitmap.isRecycled) {
            return null
        }
        // 单位：从 Byte 换算成 KB
        var currentSize = (bitmap.byteCount / 1024f).toDouble()
        //double currentSize = bitmapToByteArray(bitmap, false).length / 1024;
        // 判断bitmap占用空间是否大于允许最大空间,如果大于则压缩,小于则不压缩
        while (currentSize > maxSize) {
            // 计算bitmap的大小是maxSize的多少倍
            val multiple = currentSize / maxSize
            // 开始压缩：将宽带和高度压缩掉对应的平方根倍
            // 1.保持新的宽度和高度，与bitmap原来的宽高比率一致
            // 2.压缩后达到了最大大小对应的新bitmap，显示效果最好
            bitmap = getZoomImage(
                bitmap,
                bitmap!!.width / sqrt(multiple),
                bitmap.height / sqrt(multiple)
            )
            //currentSize = bitmapToByteArray(bitmap, false).length / 1024;
            currentSize = (bitmap!!.byteCount / 1024f).toDouble()
        }
        return bitmap
    }

    /**
     * 图片的缩放方法
     *
     * @param orgBitmap ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     */
    private fun getZoomImage(orgBitmap: Bitmap?, newWidth: Double, newHeight: Double): Bitmap? {
        if (null == orgBitmap) {
            return null
        }
        if (orgBitmap.isRecycled) {
            return null
        }
        if (newWidth <= 0 || newHeight <= 0) {
            return null
        }

        // 获取图片的宽和高
        val width = orgBitmap.width.toFloat()
        val height = orgBitmap.height.toFloat()
        // 创建操作图片的matrix对象
        val matrix = Matrix()
        // 计算宽高缩放率
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight)
        return Bitmap.createBitmap(orgBitmap, 0, 0, width.toInt(), height.toInt(), matrix, true)
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    fun readPictureDegree(context: Context, path: String): Int {
        val inputStream: InputStream = FileUtil.getInputStream(context, path) ?: return 0
        var degree = 0
        try {
            val exifInterface = ExifInterface(inputStream)
            val orientation: Int = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }

    //按图片原尺寸进行旋转角度
    fun rotate(bm: Bitmap, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(1.0f, 1.0f)
        matrix.setRotate(degree.toFloat())
        return Bitmap.createBitmap(bm, 0, 0, bm.width, bm.height, matrix, true)
    }

    /**
     * view转bitmap
     * 仅限在视图上能看到的View
     */
    fun viewConversionBitmapWithScale(v: View): Bitmap? {
        var w = v.width
        var h = v.height
        if (w == 0) {
            val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(size, size)
            w = v.measuredWidth
            h = v.measuredHeight
        }
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        // 如果不设置canvas画布为白色，则生成透明
        c.drawColor(Color.WHITE)
        v.layout(0, 0, w, h)
        v.draw(c)
        c.scale(400 / w.toFloat(), 180 / h.toFloat())
        return bmp
    }

    fun viewConversionBitmap(v: View): Bitmap? {
        var w = v.width
        var h = v.height
        if (w == 0) {
            val size = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(size, size)
            w = v.measuredWidth
            h = v.measuredHeight
        }
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        v.layout(0, 0, w, h)
        v.draw(c)
        return bmp
    }

    fun bitmapToByte(bmp: Bitmap): ByteArray? {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        try {
            return baos.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                baos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
}