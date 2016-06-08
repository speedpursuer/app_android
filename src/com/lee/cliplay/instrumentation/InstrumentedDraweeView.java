/*
 * This file provided by Facebook is for non-commercial testing and evaluation
 * purposes only.  Facebook reserves all rights not expressly granted.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * FACEBOOK BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.lee.cliplay.instrumentation;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.AttributeSet;

import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lee.cliplay.ClipActivity;

import javax.annotation.Nullable;

/**
 * {@link SimpleDraweeView} with instrumentation.
 */
public class InstrumentedDraweeView extends SimpleDraweeView implements Instrumented {

  private Instrumentation mInstrumentation;
  private ControllerListener<Object> mListener;
  private Context context;
  private int position;

  public InstrumentedDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
    super(context, hierarchy);
    this.context = context;
    init();
  }

  public InstrumentedDraweeView(Context context) {
    super(context);
    this.context = context;
    init();
  }

  public InstrumentedDraweeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    init();
  }

  public InstrumentedDraweeView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    this.context = context;
    init();
  }

  private void init() {
//    mInstrumentation = new Instrumentation(this);
    mListener = new BaseControllerListener<Object>() {
      @Override
      public void onSubmit(String id, Object callerContext) {
//        mInstrumentation.onStart();
      }
      @Override
      public void onFinalImageSet(
        String id,
        @Nullable Object imageInfo,
        @Nullable Animatable animatable) {
//        mInstrumentation.onSuccess();
        if (animatable != null) {
          ClipActivity activity = (ClipActivity)context;
          if(activity.isVisible(position)) {
            animatable.start();
          }
//          FLog.w("Cliplay", "Position %s", tag);
        }
      }
      @Override
      public void onFailure(String id, Throwable throwable) {
//        mInstrumentation.onFailure(throwable);
//        FLog.e("Cliplay", "Downloading fail reason = %s", throwable.toString());
        throwable.printStackTrace();
      }
      @Override
      public void onRelease(String id) {
//        mInstrumentation.onCancellation();
      }
    };
  }

  @Override
//  public void initInstrumentation(String tag, PerfListener perfListener) {
  public void initInstrumentation(String tag) {
//    mInstrumentation.init(tag, perfListener);
    this.position = Integer.valueOf(tag).intValue();
  }

  @Override
  public void onDraw(final Canvas canvas) {
    super.onDraw(canvas);
//    mInstrumentation.onDraw(canvas);
  }

  @Override
  public void setImageURI(Uri uri, @Nullable Object callerContext) {
    SimpleDraweeControllerBuilder controllerBuilder = getControllerBuilder()
        .setUri(uri)
        .setCallerContext(callerContext)
        .setOldController(getController());
    if (controllerBuilder instanceof AbstractDraweeControllerBuilder) {
      ((AbstractDraweeControllerBuilder<?,?,?,?>) controllerBuilder)
          .setControllerListener(mListener);
    }
    setController(controllerBuilder.build());
  }

  public ControllerListener<Object> getListener() {
    return mListener;
  }
}
