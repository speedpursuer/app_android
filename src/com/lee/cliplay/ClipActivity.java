package com.lee.cliplay;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.alertdialogpro.AlertDialogPro;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lee.cliplay.Push.CustomApplication;
import com.lee.cliplay.adapters.FrescoAdapter;
import com.lee.cliplay.configs.imagepipeline.ImagePipelineConfigFactory;
import com.lee.cliplay.holders.FrescoHolder;
import com.lee.cliplay.instrumentation.InstrumentedDraweeView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xl on 16/5/19.
 */
public class ClipActivity extends Activity {

    private com.lee.cliplay.adapters.ImageListAdapter mCurrentAdapter;
    private RecyclerView mRecyclerView;

    private List<String> mImageUrls = new ArrayList<>();

    public boolean isAwayFromHere;

    private static final String TAG = "Cliplay";

    private static final int VERTICAL_ITEM_SPACE = 48;

    protected CustomApplication mMyApp;

    public String header = null;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mMyApp = (CustomApplication)this.getApplicationContext();

        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.image_grid);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, R.layout.divider));
//        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.WHITE)
                        .sizeResId(R.dimen.divider)
//                        .positionInsideItem(true)
//                        .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                        .build());

        FLog.setMinimumLoggingLevel(FLog.WARN);
        com.lee.cliplay.Drawables.init(getResources());

        if (savedInstanceState != null) {

        }

        mCurrentAdapter = null;

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            updateAutoPlay();
            super.onScrollStateChanged(recyclerView, newState);
            }
        });

        isAwayFromHere = false;

        String data = getIntent().getStringExtra("urls");

        header = getIntent().getStringExtra("pushHeader");

        setSourceAdapter(data);

        boolean showTip = getIntent().getBooleanExtra("showTip", false);

        if(showTip) addTipDialog();
    }

    private void addTipDialog() {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.tip);

        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("操作说明")
                .setMessage("点击进入滑屏慢放模式")
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
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
        if(isAwayFromHere) updateAutoPlay();
    }
    @Override
    protected void onPause() {
//        clearReferences();
        super.onPause();
        stopAllPlay();
        isAwayFromHere = true;
    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        clearReferences();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        super.onDestroy();
    }

    public void updateAutoPlay() {
        GridLayoutManager layoutManager = ((GridLayoutManager)mRecyclerView.getLayoutManager());

        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        int firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

        for(int i = firstVisiblePosition; i < lastVisiblePosition + 1; i++) {
            FrescoHolder vh = (FrescoHolder) mRecyclerView.findViewHolderForLayoutPosition(i);
            InstrumentedDraweeView view = vh.getImageView();
            Animatable animation = view.getController().getAnimatable();

            if (animation != null) {
                if (i < firstCompletelyVisiblePosition) {
                    animation.stop();
                } else if (i <= lastCompletelyVisiblePosition) {
                    if (!animation.isRunning()) {
                        animation.start();
                    }
                } else {
                    animation.stop();
                }
            }
        }
    }

    public void stopAllPlay() {
        GridLayoutManager layoutManager = ((GridLayoutManager)mRecyclerView.getLayoutManager());

        int firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

        if(firstCompletelyVisiblePosition == -1 || lastCompletelyVisiblePosition == -1) return;

        for(int i = firstCompletelyVisiblePosition; i <= lastCompletelyVisiblePosition; i++) {
            FrescoHolder vh = (FrescoHolder) mRecyclerView.findViewHolderForLayoutPosition(i);
            InstrumentedDraweeView view = vh.getImageView();
            Animatable animation = view.getController().getAnimatable();
            if (animation != null) {
                if (animation.isRunning()) {
                    animation.stop();
                }
            }
        }
    }

    public boolean isVisible(int position) {
        GridLayoutManager layoutManager = ((GridLayoutManager)mRecyclerView.getLayoutManager());

        int firstCompletelyVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        int lastCompletelyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

        if(position >= firstCompletelyVisiblePosition && position <= lastCompletelyVisiblePosition) return true;

        return false;
    }

    public static int calcDesiredSize(Context context, int parentWidth, int parentHeight) {
        int orientation = context.getResources().getConfiguration().orientation;
        int desiredSize = (orientation == Configuration.ORIENTATION_LANDSCAPE) ?
                parentHeight / 2 : parentHeight / 3;
        return Math.min(desiredSize, parentWidth);
    }

    private void setSourceAdapter(String data) {
        mImageUrls.clear();
        loadNetworkUrls(data);
        setLoaderAdapter();
    }

    private void loadNetworkUrls(String string_of_json_array) {

        List<String> urls = new ArrayList<>();

        try{
            JSONObject dataJson = new JSONObject(string_of_json_array);
            String url = dataJson.getString("image");
            if(header == null) {
                header = dataJson.getString("header");
            }

            this.setTitle(header);

            JSONArray array = new JSONArray(url);
            for (int i = 0; i < array.length(); i++) {
                String a = (String)array.get(i);
                urls.add(a);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        mImageUrls = urls;

        updateAdapter(mImageUrls);
    }

    private void updateAdapter(List<String> urls) {
        if (mCurrentAdapter != null) {
            mCurrentAdapter.clear();
            if (urls != null) {
                for (String url : urls) {
                    mCurrentAdapter.addUrl(url);
                }
            }
            mCurrentAdapter.notifyDataSetChanged();
        }
    }

    private void setLoaderAdapter () {

        resetAdapter();

        mCurrentAdapter = new FrescoAdapter(
            this,
            ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this)
        );

        checkConnection();

        mRecyclerView.setAdapter(mCurrentAdapter);

        updateAdapter(mImageUrls);
    }

    private void checkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if(networkInfo == null || networkInfo.getTypeName() == null) {
            mMyApp.showDialog("无法下载动图", "请确认互联网连接。", false, this);
        }else if(networkInfo.getTypeName().equals("WIFI")) {
            ImagePipelineConfigFactory.setMaxRequests(2);
        }else {
            ImagePipelineConfigFactory.setMaxRequests(1);
        }
    }

    private void resetAdapter() {
        if (mCurrentAdapter != null) {
            mCurrentAdapter.shutDown();
            mCurrentAdapter = null;
            System.gc();
        }
    }

    private void clearReferences(){
        Activity currActivity = mMyApp.getCurrentActivity();
        if (this.equals(currActivity))
            mMyApp.setCurrentActivity(null);
    }

    public void showDialog() {

//        TextView view = new TextView(this);
//        view.setText("无法同步最新数据");
//        view.setGravity(Gravity.CENTER);
//        view.setTextColor(Color.WHITE);
//        view.setTextSize(20);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        builder.setCustomTitle(view)
//                .setMessage("请确认互联网连接")
//                .setNegativeButton("好", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                });
//
//        builder.create().show();


//        ImageView imageView = new ImageView(this);
//        imageView.setImageResource(R.drawable.tip);

        AlertDialogPro.Builder builder = new AlertDialogPro.Builder(this);
        builder.setTitle("无法下载动图")
                .setMessage("请确认互联网连接。")
                .setPositiveButton("好", new ButtonClickedListener("Dismiss"))
                .show();
    }
}
