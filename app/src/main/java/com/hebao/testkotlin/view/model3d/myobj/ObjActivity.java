package com.hebao.testkotlin.view.model3d.myobj;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.hebao.testkotlin.R;
import com.hebao.testkotlin.view.model3d.plane.PlaneGlSurfaceView;

import androidx.appcompat.app.AppCompatActivity;

public class ObjActivity extends AppCompatActivity {

    private PlaneGlSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_obj);
        mGLView = findViewById(R.id.glsv_plane);
        GokuRenderer gokuRenderer = new GokuRenderer(mGLView);
        mGLView.setRenderer(gokuRenderer);
        // 渲染模式(被动渲染)
        mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLView.setOnTouchListener(gokuRenderer.getEventListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

}
