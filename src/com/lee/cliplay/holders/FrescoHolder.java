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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lee.cliplay.GifActivity;
import com.lee.cliplay.instrumentation.InstrumentedDraweeView;

import java.io.File;

/**
 * This is the Holder class for the RecycleView to use with Fresco
 */
public class FrescoHolder extends BaseViewHolder<InstrumentedDraweeView> implements View.OnClickListener{

//  public FrescoHolder(
//      Context context, View parentView,
//      InstrumentedDraweeView intrumentedDraweeView) {
////    InstrumentedDraweeView intrumentedDraweeView, PerfListener perfListener) {
////    super(context, parentView, intrumentedDraweeView, perfListener);
//    super(context, parentView, intrumentedDraweeView);
//    intrumentedDraweeView.setOnClickListener(this);
//  }

  public FrescoHolder(
          Context context, View view, GenericDraweeHierarchy hierarchy, View parentView) {
    super(context, view, parentView);
    mImageView.setHierarchy(hierarchy);
    mImageView.setOnClickListener(this);
  }

  @Override
  protected void onBind(String uriString, String desc) {
    Uri uri = Uri.parse(uriString);
    ImageRequestBuilder imageRequestBuilder =
        ImageRequestBuilder.newBuilderWithSource(uri);
    if (UriUtil.isNetworkUri(uri)) {
      imageRequestBuilder.setProgressiveRenderingEnabled(true);
    } else {
      imageRequestBuilder.setResizeOptions(new ResizeOptions(
          mImageView.getLayoutParams().width,
          mImageView.getLayoutParams().height));
    }
    DraweeController draweeController = Fresco.newDraweeControllerBuilder()
        .setImageRequest(imageRequestBuilder.build())
        .setTapToRetryEnabled(false)
        .setOldController(mImageView.getController())
        .setControllerListener(mImageView.getListener())
        .setAutoPlayAnimations(false)
        .build();
    mImageView.setController(draweeController);
    URL = uriString;

    if(desc.equals("")) {
      mTextView.setVisibility(View.GONE);
    }else {
      mTextView.setText(desc);
    }
  }

  @Override
  public void onClick(View view) {
    File f = getCachedImageOnDisk(Uri.parse(URL));
    Animatable animation = ((InstrumentedDraweeView)view).getController().getAnimatable();
    if(f == null || animation == null) {
        return;
    }

    if (animation.isRunning()) {
        animation.stop();
    }

    Activity activity = (Activity)getMainActivity();

    Intent intent = new Intent(activity, GifActivity.class);
    intent.putExtra("filePath", f.getAbsolutePath());
    activity.startActivity(intent);
  }

  public static File getCachedImageOnDisk(Uri loadUri) {
    File localFile = null;
    if (loadUri != null) {
      CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
      if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
        BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
        localFile = ((FileBinaryResource) resource).getFile();
      } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
        BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
        localFile = ((FileBinaryResource) resource).getFile();
      }
    }
    return localFile;
  }
}
