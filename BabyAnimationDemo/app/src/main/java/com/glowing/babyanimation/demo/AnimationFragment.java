package com.glowing.babyanimation.demo;


import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.cocos2dx.lib.Cocos2dxGLSurfaceView;
import org.cocos2dx.lib.Cocos2dxHandler;
import org.cocos2dx.lib.Cocos2dxHelper;
import org.cocos2dx.lib.Cocos2dxRenderer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnimationFragment extends Fragment implements Cocos2dxHelper.Cocos2dxHelperListener {
// ===========================================================
  // Constants
  // ===========================================================

  private final static String TAG = AnimationFragment.class.getSimpleName();

  // ===========================================================
  // Fields
  // ===========================================================

  private Cocos2dxGLSurfaceView mGLSurfaceView = null;
  private int[] mGLContextAttrs = null;
  private Cocos2dxHandler mHandler = null;
  private static Context sContext = null;

  public static Context getContext() {
    return sContext;
  }

  protected void onLoadNativeLibraries() {
    try {
      System.loadLibrary("cocos2dcpp");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // ===========================================================
  // Constructors
  // ===========================================================

  @Override
  public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    onLoadNativeLibraries();

    sContext = getActivity();
    this.mHandler = new Cocos2dxHandler(sContext);

    Cocos2dxHelper.init(this);

    this.mGLContextAttrs = Cocos2dxRenderer.getGLContextAttrs();
    this.init();
  }


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return mFrameLayout;
  }

  // ===========================================================
  // Getter & Setter
  // ===========================================================

  // ===========================================================
  // Methods for/from SuperClass/Interfaces
  // ===========================================================

  @Override
  public void onResume() {
    super.onResume();

    Cocos2dxHelper.onResume();
    this.mGLSurfaceView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();

    Cocos2dxHelper.onPause();
    this.mGLSurfaceView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void showDialog(final String pTitle, final String pMessage) {

  }

  @Override
  public void showEditTextDialog(final String pTitle, final String pContent, final int pInputMode, final int pInputFlag, final int pReturnType, final int pMaxLength) {

  }

  @Override
  public void runOnGLThread(final Runnable pRunnable) {
    this.mGLSurfaceView.queueEvent(pRunnable);
  }

  protected FrameLayout mFrameLayout = null;
  // ===========================================================
  // Methods
  // ===========================================================
  public void init() {

    // FrameLayout
    ViewGroup.LayoutParams framelayout_params =
        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);
    mFrameLayout = new FrameLayout(getActivity());
    mFrameLayout.setLayoutParams(framelayout_params);

    // Cocos2dxGLSurfaceView
    this.mGLSurfaceView = this.onCreateView();

    // ...add to FrameLayout
    mFrameLayout.addView(this.mGLSurfaceView);

    // Switch to supported OpenGL (ARGB888) mode on emulator
    if (isAndroidEmulator())
      this.mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

    this.mGLSurfaceView.setCocos2dxRenderer(new Cocos2dxRenderer());
  }

  public Cocos2dxGLSurfaceView onCreateView() {
    Cocos2dxGLSurfaceView glSurfaceView = new Cocos2dxGLSurfaceView(getActivity());
    //this line is need on some device if we specify an alpha bits
    if(this.mGLContextAttrs[3] > 0) glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

    class cocos2dEGLConfigChooser implements GLSurfaceView.EGLConfigChooser
    {
      protected int[] configAttribs;
      public cocos2dEGLConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize)
      {
        configAttribs = new int[] {redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize};
      }
      public cocos2dEGLConfigChooser(int[] attribs)
      {
        configAttribs = attribs;
      }

      public EGLConfig selectConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs, int[] attribs)
      {
        for (EGLConfig config : configs) {
          int d = findConfigAttrib(egl, display, config,
              EGL10.EGL_DEPTH_SIZE, 0);
          int s = findConfigAttrib(egl, display, config,
              EGL10.EGL_STENCIL_SIZE, 0);
          if ((d >= attribs[4]) && (s >= attribs[5])) {
            int r = findConfigAttrib(egl, display, config,
                EGL10.EGL_RED_SIZE, 0);
            int g = findConfigAttrib(egl, display, config,
                EGL10.EGL_GREEN_SIZE, 0);
            int b = findConfigAttrib(egl, display, config,
                EGL10.EGL_BLUE_SIZE, 0);
            int a = findConfigAttrib(egl, display, config,
                EGL10.EGL_ALPHA_SIZE, 0);
            if ((r >= attribs[0]) && (g >= attribs[1])
                && (b >= attribs[2]) && (a >= attribs[3])) {
              return config;
            }
          }
        }
        return null;
      }

      private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                                   EGLConfig config, int attribute, int defaultValue) {
        int[] value = new int[1];
        if (egl.eglGetConfigAttrib(display, config, attribute, value)) {
          return value[0];
        }
        return defaultValue;
      }

      @Override
      public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
      {
        int[] numConfigs = new int[1];
        if(egl.eglGetConfigs(display, null, 0, numConfigs))
        {
          EGLConfig[] configs = new EGLConfig[numConfigs[0]];
          int[] EGLattribs = {
              EGL10.EGL_RED_SIZE, configAttribs[0],
              EGL10.EGL_GREEN_SIZE, configAttribs[1],
              EGL10.EGL_BLUE_SIZE, configAttribs[2],
              EGL10.EGL_ALPHA_SIZE, configAttribs[3],
              EGL10.EGL_DEPTH_SIZE, configAttribs[4],
              EGL10.EGL_STENCIL_SIZE,configAttribs[5],
              EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
              EGL10.EGL_NONE
          };
          int[] choosedConfigNum = new int[1];

          egl.eglChooseConfig(display, EGLattribs, configs, numConfigs[0], choosedConfigNum);
          if(choosedConfigNum[0]>0)
          {
            return selectConfig(egl, display, configs, configAttribs);
          }
          else
          {
            int[] defaultEGLattribs = {
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_ALPHA_SIZE, 0,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE,0,
                EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE
            };
            int[] defaultEGLattribsAlpha = {
                EGL10.EGL_RED_SIZE, 4,
                EGL10.EGL_GREEN_SIZE, 4,
                EGL10.EGL_BLUE_SIZE, 4,
                EGL10.EGL_ALPHA_SIZE, 4,
                EGL10.EGL_DEPTH_SIZE, 0,
                EGL10.EGL_STENCIL_SIZE,0,
                EGL10.EGL_RENDERABLE_TYPE, 4, //EGL_OPENGL_ES2_BIT
                EGL10.EGL_NONE
            };
            int[] attribs = null;
            //choose one can use
            if(this.configAttribs[3] == 0)
            {
              egl.eglChooseConfig(display, defaultEGLattribs, configs, numConfigs[0], choosedConfigNum);
              attribs = new int[]{5,6,5,0,0,0};
            }
            else
            {
              egl.eglChooseConfig(display, defaultEGLattribsAlpha, configs, numConfigs[0], choosedConfigNum);
              attribs = new int[]{4,4,4,4,0,0};
            }
            if(choosedConfigNum[0] > 0)
            {
              return selectConfig(egl, display, configs, attribs);
            }
            else
            {
              Log.e("DEVICE_POLICY_SERVICE", "Can not select an EGLConfig for rendering.");
              return null;
            }
          }
        }
        Log.e("DEVICE_POLICY_SERVICE", "Can not select an EGLConfig for rendering.");
        return null;
      }

    }
    cocos2dEGLConfigChooser chooser = new cocos2dEGLConfigChooser(this.mGLContextAttrs);
    glSurfaceView.setEGLConfigChooser(chooser);

    return glSurfaceView;
  }

  private final static boolean isAndroidEmulator() {
    String model = Build.MODEL;
    Log.d(TAG, "model=" + model);
    String product = Build.PRODUCT;
    Log.d(TAG, "product=" + product);
    boolean isEmulator = false;
    if (product != null) {
      isEmulator = product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_");
    }
    Log.d(TAG, "isEmulator=" + isEmulator);
    return isEmulator;
  }

  // ===========================================================
  // Inner and Anonymous Classes
  // ===========================================================
}
