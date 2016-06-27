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
import android.support.v7.widget.RecyclerView;

import com.lee.cliplay.holders.BaseViewHolder;
import com.lee.cliplay.holders.HeaderViewHolder;
import com.lee.cliplay.holders.TitleViewHolder;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for RecyclerView Adapters
 */
public abstract class ImageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

//  private final PerfListener mPerfListener;

  protected static final int TYPE_HEADER = 0;
  protected static final int TYPE_ITEM = 1;
  protected static final int TYPE_TITLE = 2;
  private final Context mContext;

  private List<Clip> mModel;
  private String header;

//  public ImageListAdapter(final Context context, final PerfListener perfListener) {
  public ImageListAdapter(final Context context) {
    this.mContext = context;
//    this.mPerfListener = perfListener;
    this.mModel = new LinkedList<Clip>();
  }

  public void addUrl(final Clip clip) {
    mModel.add(clip);
  }

//  protected PerfListener getPerfListener() {
//    return mPerfListener;
//  }

  protected Clip getItem(final int position) {
    return mModel.get(position - 1);
  }

  private boolean isPositionHeader(int position) {
    return position == 0;
  }

  private boolean isPositionTitle(int position) {
    Clip clip = getItem(position);
    return !clip.getDesc().equals("");
  }

  @Override
  public int getItemViewType(int position) {
    if (isPositionHeader(position)) {
      return TYPE_HEADER;
    }else if (isPositionTitle(position)){
      return TYPE_TITLE;
    }else {
      return TYPE_ITEM;
    }
  }

  @Override
  public int getItemCount() {
    return mModel.size() + 1;
  }

  protected Context getContext() {
    return mContext;
  }

  public void clear() {
    mModel.clear();
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof BaseViewHolder) {
      Clip clip = getItem(position);
      ((BaseViewHolder)holder).bind(clip.getUrl(), position);
    }else if (holder instanceof HeaderViewHolder) {
      ((HeaderViewHolder)holder).setHeaderText(header);
    }else {
      Clip clip = getItem(position);
      ((TitleViewHolder)holder).setTitle(clip.getDesc());
    }
  }

  public void setHeader(String header) {
    this.header = header;
  }

  /**
   * Releases any resources and tears down the adapter.
   */
  public abstract void shutDown();
}
