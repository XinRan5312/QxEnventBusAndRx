
package com.xinran.studyenventbus;


import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xinran.studyenventbus.dummy.DummyContent;
import com.xinran.studyenventbus.rxjava.RxEnventBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import rx.Observer;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;


public class ItemListFragment extends ListFragment {
    private CompositeSubscription subscription;
    public ItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register
//        EventBus.getDefault().register(this);
        //用RxEnventBus替换EnventBus
        subscription=new CompositeSubscription();
        subscription.add(RxEnventBus.getInstance().toSubscription(Event.ItemListEvent.class, new Observer<Event.ItemListEvent>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Event.ItemListEvent itemListEvent) {
                Log.d(MainActivity.TAG,
                        "Received ItemListEvent, is main thread:" + (Looper.myLooper() == Looper.getMainLooper()));
                setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
                        android.R.layout.simple_list_item_activated_1, android.R.id.text1, itemListEvent.getItems()));
            }
        }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister
//        EventBus.getDefault().unregister(this);
        subscription.unsubscribe();
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // 开启工作线程加载列表
        new WorkerThread().start();
    }
    
    /** 在主线程接收ItemListEvent事件，必须是public void */
//    @Subscribe
//    public void onEventMainThread(Event.ItemListEvent event) {
//        Log.d(MainActivity.TAG,
//                "Received ItemListEvent, is main thread:" + (Looper.myLooper() == Looper.getMainLooper()));
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                android.R.layout.simple_list_item_activated_1, android.R.id.text1, event.getItems()));
//    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        // 发送列表项点击事件，直接使用getItem，这里是DummyItem类型
        Log.d(MainActivity.TAG, "Clicked item:" + position);
//        EventBus.getDefault().post(getListView().getItemAtPosition(position));
        RxEnventBus.getInstance().post(getListView().getItemAtPosition(position));
    }

    /** 加载列表的工作线程 */
    private static class WorkerThread extends Thread {
        
        @Override
        public void run() {

                Log.d(MainActivity.TAG, "Start get data at WorkerThred");

                // 发事件，在后台线程发的事件
            Log.d(MainActivity.TAG, "Got data, post ItemListEvent");
//                EventBus.getDefault().post(new Event.ItemListEvent(DummyContent.ITEMS));
            //用RxEnventBus替换EnventBus
            RxEnventBus.getInstance().post(new Event.ItemListEvent(DummyContent.ITEMS));

        }
    }
}
