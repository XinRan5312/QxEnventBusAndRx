
package com.xinran.studyenventbus;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xinran.studyenventbus.dummy.DummyContent;
import com.xinran.studyenventbus.rxjava.RxEnventBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class ItemDetailFragment extends Fragment {
private CompositeSubscription mCompositeSubcription;
    private TextView tvDetail;

    private DummyContent.DummyItem mItem;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register
        mCompositeSubcription=new CompositeSubscription();
//        EventBus.getDefault().register(this);
        mCompositeSubcription.add(RxEnventBus.getInstance().toSubscription(DummyContent.DummyItem.class, new Action1<DummyContent.DummyItem>() {
            @Override
            public void call(DummyContent.DummyItem dummyItem) {
                Log.d(MainActivity.TAG, "Received event at ItemDetailFragment");
                mItem = dummyItem;
                updateDetail();
            }
        }));
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister
//        EventBus.getDefault().unregister(this);
        mCompositeSubcription.unsubscribe();
    }

    /** List点击时会发送些事件，接收到事件后更新详情 */
//    @Subscribe
//    public void onEventMainThread(DummyContent.DummyItem item) {
//        Log.d(MainActivity.TAG, "Received event at ItemDetailFragment");
//        mItem = item;
//        updateDetail();
//    }

    private void updateDetail() {
        if (mItem != null) {
            tvDetail.setText(mItem.content);
        }
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        tvDetail = (TextView)rootView.findViewById(R.id.item_detail);
        return rootView;
    }
}
