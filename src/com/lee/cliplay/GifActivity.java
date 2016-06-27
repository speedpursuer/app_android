package com.lee.cliplay;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alertdialogpro.AlertDialogPro;
import com.lee.cliplay.configs.LocalDataMgr;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pl.droidsonroids.gif.GifTexImage2D;
import pl.droidsonroids.gif.InputSource;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by 06peng on 2015/6/25.
 */
public class GifActivity extends Activity {

    private GifTexImage2D mGifTexImage2D;
    private int screenWidth;
    private int screenHeight;
    private int noOfFrames;
    private float startPoint;
    private boolean isRunning = false;
    private static final String TIP_FLAG = "showGifTip";

    private static final String VERTEX_SHADER_CODE =
            "attribute vec4 position;" +
                    "attribute vec4 coordinate;" +
                    "varying vec2 textureCoordinate;" +
                    "void main()" +
                    "{" +
                    "    gl_Position = position;" +
                    "    textureCoordinate = vec2(coordinate.s, 1.0 - coordinate.t);" +
                    "}";

    private static final String FRAGMENT_SHADER_CODE =
//            "varying mediump vec2 textureCoordinate;" +
            "varying lowp vec2 textureCoordinate;" +
                    "uniform sampler2D texture;" +
                    "void main() { " +
                    "    gl_FragColor = texture2D(texture, textureCoordinate);" +
                    "}";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);

        String filePath = getIntent().getStringExtra("filePath");

        try {
            mGifTexImage2D = new GifTexImage2D(new InputSource.FileSource(filePath), null);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        if (!supportsEs2()) {
//            Snackbar.make(container, R.string.gles2_not_supported, Snackbar.LENGTH_LONG).show();
            return;
        }

        final GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surfaceview);

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new Renderer());

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                boolean isMoved = Math.abs(startPoint - event.getX()) > 20? true: false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startPoint = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(!isMoved) {
                            repeat();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(isMoved) {
                            stop();
                            goToFrames(event.getX());
                        }
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

//        glSurfaceView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                synchronized (GifActivity.this) {
////
////                }
//                if(isRunning) {
//                    mGifTexImage2D.stopDecoderThread();
//                    isRunning = false;
//                }else {
//                    mGifTexImage2D.startDecoderThread();
//                    isRunning = true;
//                }
//            }
//        });


//        SeekBar seekbar = (SeekBar) findViewById(R.id.seekbar);
//        seekbar.setMax(mGifTexImage2D.getNumberOfFrames()-1);
//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser){
////                    mGifTexImage2D.seekToFrame(progress);
//                    synchronized (GifActivity.this) {
//                        mGifTexImage2D.seekToFrame(progress);
//                    }
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        noOfFrames = mGifTexImage2D.getNumberOfFrames();

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        screenHeight= metrics.heightPixels;

        mGifTexImage2D.seekToFrame(0);

        play();

        setLayout(glSurfaceView);

        if(LocalDataMgr.getShowTipFlag(TIP_FLAG)) addTipDialog();
    }

    private void play(){
        if(!isRunning) {
            mGifTexImage2D.startDecoderThread();
            isRunning = true;
        }
    }

    private void stop(){
        if(isRunning) {
            mGifTexImage2D.stopDecoderThread();
            isRunning = false;
        }
    }

    private void repeat(){
        if(isRunning) {
            mGifTexImage2D.stopDecoderThread();
            isRunning = false;
        }else {
            mGifTexImage2D.startDecoderThread();
            isRunning = true;
        }
    }

    public void goToFrames(float position) {
        int frameToGO = Math.round(position/screenWidth * noOfFrames);

        frameToGO = frameToGO > noOfFrames - 1? noOfFrames - 1: frameToGO;

        synchronized (GifActivity.this) {
            mGifTexImage2D.seekToFrame(frameToGO);
        }
    }

    private void setLayout(GLSurfaceView view) {
        float imageWidthToHeight = ((float) mGifTexImage2D.getWidth()) / ((float) mGifTexImage2D.getHeight());
        float viewWidthToHeight = (float)screenWidth / (float)screenHeight;
        float top = 0, left = 0, imageViewWidth, imageViewHeight;

        if(viewWidthToHeight > imageWidthToHeight) {
            imageViewWidth = screenHeight * imageWidthToHeight;
            imageViewHeight = screenHeight;
            left = (screenWidth - imageViewWidth) / 2;
        }else {
            imageViewWidth = screenWidth;
            imageViewHeight = screenWidth / imageWidthToHeight;
            top = (screenHeight - imageViewHeight) / 2;
        }

//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams((int)imageViewWidth, (int)imageViewHeight);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)imageViewWidth, (int)imageViewHeight);
        layoutParams.setMargins((int)left, (int)top, (int)left, (int)top);
        view.setLayoutParams(layoutParams);
    }

    private class Renderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            int[] texNames = {0};
            glGenTextures(1, texNames, 0);
            glBindTexture(GL_TEXTURE_2D, texNames[0]);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

            final int vertexShader = loadShader(GL_VERTEX_SHADER, VERTEX_SHADER_CODE);
            final int pixelShader = loadShader(GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE);
            final int program = glCreateProgram();
            glAttachShader(program, vertexShader);
            glAttachShader(program, pixelShader);
            glLinkProgram(program);
            int position = glGetAttribLocation(program, "position");
            int texture = glGetUniformLocation(program, "texture");
            int coordinate = glGetAttribLocation(program, "coordinate");
            glUseProgram(program);

            FloatBuffer textureBuffer = createFloatBuffer(new float[]{0, 0, 1, 0, 0, 1, 1, 1});
            FloatBuffer verticesBuffer = createFloatBuffer(new float[]{-1, -1, 1, -1, -1, 1, 1, 1});
            glVertexAttribPointer(coordinate, 2, GL_FLOAT, false, 0, textureBuffer);
            glEnableVertexAttribArray(coordinate);
            glUniform1i(texture, 0);
            glVertexAttribPointer(position, 2, GL_FLOAT, false, 0, verticesBuffer);
            glEnableVertexAttribArray(position);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, mGifTexImage2D.getWidth(), mGifTexImage2D.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
//            FLog.w("Cliplay", "mGifTexImage2D.getWidth(): %d", mGifTexImage2D.getWidth());
//            FLog.w("Cliplay", "mGifTexImage2D.getHeight(): %d", mGifTexImage2D.getHeight());
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //no-op
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            synchronized (GifActivity.this) {
                mGifTexImage2D.glTexSubImage2D(GL_TEXTURE_2D, 0);
            }
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    private static int loadShader(int shaderType, String source) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, source);
        glCompileShader(shader);
        return shader;
    }

    private static FloatBuffer createFloatBuffer(float[] floats) {
        FloatBuffer fb = ByteBuffer
                .allocateDirect(floats.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb.put(floats);
        fb.rewind();
        return fb;
    }

    private boolean isOpenGLES2Supported() {
        FeatureInfo[] featureInfos = this.getPackageManager().getSystemAvailableFeatures();
        if (featureInfos != null) {
            for (FeatureInfo featureInfo : featureInfos) {
                if (featureInfo.name == null) {
                    return ((featureInfo.reqGlEsVersion & 0xffff0000) >> 16) >= 2;
                }
            }
        }

        return false;
    }

    private boolean supportsEs2() {

        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        return supportsEs2;
    }

    private void addTipDialog() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.tip);

        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("操作说明")
                .setMessage("图中点击暂停／播放。滑动前进后退。")
                .setPositiveButton("知道了", new ButtonClickedListener("Dismiss"))
                .setView(imageView)
                .show();
    }

    private class ButtonClickedListener implements DialogInterface.OnClickListener {
        private CharSequence mShowWhenClicked;

        public ButtonClickedListener(CharSequence showWhenClicked) {
            mShowWhenClicked = showWhenClicked;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            LocalDataMgr.setShowTipFlag(TIP_FLAG);
        }
    }
}
