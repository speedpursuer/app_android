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

package com.lee.cliplay.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lee.cliplay.ClipActivity;
import com.lee.cliplay.R;
import com.lee.cliplay.instrumentation.Instrumented;

/**
 * The base ViewHolder with instrumentation
 */
public abstract class BaseViewHolder<V extends View & Instrumented>
    extends RecyclerView.ViewHolder {

//  private final PerfListener mPerfListener;
  private final View mParentView;
  protected final V mImageView;
  protected TextView mTextView;
  private Context mContext;
  protected String URL;
  private int screenHeight;

//  public BaseViewHolder(
//      Context context,
//      View parentView,
//      V imageView) {
////      PerfListener perfListener) {
//    super(imageView);
//    this.mContext = context;
////    this.mPerfListener = perfListener;
//    this.mParentView = parentView;
//    this.mImageView = imageView;
//    this.screenHeight = this.mContext.getResources().getDisplayMetrics().heightPixels;
//
//    if (mParentView != null) {
//      int size = calcDesiredSize(mParentView.getWidth(), mParentView.getHeight());
//      updateViewLayoutParams(mImageView, mParentView.getWidth(), size);
//    }
//  }

  public BaseViewHolder(Context context, View view, View parentView) {
    super(view);
    this.mContext = context;
    this.mParentView = parentView;
    this.mImageView = (V)view.findViewById(R.id.id_index_gallery_item_image);
    this.mTextView = (TextView)view.findViewById(R.id.id_index_gallery_item_text);

    this.screenHeight = this.mContext.getResources().getDisplayMetrics().heightPixels;

    if (mParentView != null) {
      int size = calcDesiredSize(mParentView.getWidth(), mParentView.getHeight());
      updateViewLayoutParams(mImageView, mParentView.getWidth(), size);
    }
  }

  public void bind(String url, String desc, int position) {
    mImageView.initInstrumentation(Integer.toString(position));
//    mImageView.initInstrumentation(Integer.toString(position), mPerfListener);
    onBind(url, desc);
  }

  public V getImageView() {
    return mImageView;
  }

  public Context getMainActivity() {return mContext;}

  /**
   * Load an image of the specified uri into the view, asynchronously.
   */
  protected abstract void onBind(String uri, String desc);

  protected Context getContext() {
    return mContext;
  }

  private void updateViewLayoutParams(View view, int width, int height) {
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    if (layoutParams == null || layoutParams.height != width || layoutParams.width != height) {
//      layoutParams = new AbsListView.LayoutParams(width, height);
//      layoutParams = new RelativeLayout.LayoutParams(width, (int)(screenHeight/2.5));
      layoutParams = new LinearLayout.LayoutParams(width, (int)(screenHeight/2.5));
      view.setLayoutParams(layoutParams);
    }
  }

  private int calcDesiredSize(int parentWidth, int parentHeight) {
    return ClipActivity.calcDesiredSize(mContext, parentWidth, parentHeight);
  }
}
