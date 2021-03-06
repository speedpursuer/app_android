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

package com.lee.cliplay.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.lee.cliplay.Drawables;
import com.lee.cliplay.R;
import com.lee.cliplay.holders.FrescoHolder;
import com.lee.cliplay.holders.HeaderViewHolder;
import com.lee.cliplay.holders.TitleViewHolder;

/**
 * RecyclerView Adapter for Fresco
 */
public class FrescoAdapter extends com.lee.cliplay.adapters.ImageListAdapter {

  public FrescoAdapter(
      Context context,
//      PerfListener perfListener,
      ImagePipelineConfig imagePipelineConfig) {
//    super(context, perfListener);
    super(context);
    Fresco.initialize(context, imagePipelineConfig);
  }

  private LayoutInflater mInflater;

  public FrescoAdapter(Context context) {
    super(context);
    mInflater = LayoutInflater.from(context);
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    if(viewType == TYPE_ITEM) {
      ProgressBarDrawable pb = new ProgressBarDrawable();
      pb.setBackgroundColor(Color.parseColor("#e1e4eb"));
      pb.setColor(Color.rgb(255, 64, 0));
      pb.setHideWhenZero(true);
      pb.setPadding(0);
      GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(getContext().getResources())
              .setPlaceholderImage(Drawables.sPlaceholderDrawable)
              .setFailureImage(Drawables.sErrorDrawable)
              .setProgressBarImage(pb)
              .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
              .build();

      View view = mInflater.inflate(R.layout.activity_list_item,
              parent, false);

//    RecyclerView.ViewHolder viewHolder = new FrescoHolder(getContext(), parent, view);

//    final InstrumentedDraweeView instrView = new InstrumentedDraweeView(getContext(), gdh);

//    return new FrescoHolder(getContext(), parent, instrView, getPerfListener());
//    return new FrescoHolder(getContext(), parent, instrView);

      return new FrescoHolder(getContext(), view, gdh, parent);
    }else if(viewType == TYPE_TITLE) {
      View v = mInflater.inflate (R.layout.title_item, parent, false);
      return new TitleViewHolder(v);
    }else {
      View v = mInflater.inflate (R.layout.header_item, parent, false);
      return new HeaderViewHolder(v);
    }
  }

  @Override
  public void shutDown() {
    Fresco.shutDown();
  }
}
