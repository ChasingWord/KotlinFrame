package com.hebao.testkotlin.view.model3d.base;


import com.hebao.testkotlin.view.model3d.plane.PlaneGlSurfaceView;

/**
 * 基础的obj实体类
 */
public class GLGroup {
    /**
     * 上下文对象
     */
    private final PlaneGlSurfaceView mBaseScene;

    /**
     * 构造方法
     */
    public GLGroup(PlaneGlSurfaceView scene) {
        this.mBaseScene = scene;
    }

    /**
     * 获取上下文对象
     */
    public PlaneGlSurfaceView getBaseScene() {
        return mBaseScene;
    }

    /**
     * 物体的属性值
     */
    // 缩放大小
    protected float mSpriteScale = 1f;
    // alpha数值
    protected float mSpriteAlpha = 1;
    // 旋转
    protected float mSpriteAngleX = 0;
    protected float mSpriteAngleY = 0;
    protected float mSpriteAngleZ = 0;

    public float getSpriteScale() {
        return mSpriteScale;
    }

    public void setSpriteScale(float mSpriteScale) {
        this.mSpriteScale = mSpriteScale;
    }

    public float getSpriteAlpha() {
        return mSpriteAlpha;
    }

    public void setSpriteAlpha(float mSpriteAlpha) {
        this.mSpriteAlpha = mSpriteAlpha;
    }

    public float getSpriteAngleX() {
        return mSpriteAngleX;
    }

    public void setSpriteAngleX(float mSpriteAngleX) {
        this.mSpriteAngleX = mSpriteAngleX;
    }

    public float getSpriteAngleY() {
        return mSpriteAngleY;
    }

    public void setSpriteAngleY(float mSpriteAngleY) {
        this.mSpriteAngleY = mSpriteAngleY;
    }

    public float getSpriteAngleZ() {
        return mSpriteAngleZ;
    }

    public void setSpriteAngleZ(float mSpriteAngleZ) {
        this.mSpriteAngleZ = mSpriteAngleZ;
    }

    /**
     * 绘制方法
     */
    public void onDraw(MatrixState matrixState) {
    }
}
