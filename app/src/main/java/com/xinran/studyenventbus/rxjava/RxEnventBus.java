package com.xinran.studyenventbus.rxjava;


import rx.Observer;
import rx.Observable;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by qixinh on 16/3/31.
 */
public class RxEnventBus {
    /**
     * 利用Rxjava代替EnventBus就是不方便控制回调后执行的线程类型是主线程还是子线程
     */

    private static volatile RxEnventBus instance;
    private final SerializedSubject<Object, Object> subject;

    private RxEnventBus() {
        subject = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxEnventBus getInstance() {
        if (instance == null) {
            synchronized (RxEnventBus.class) {
                if (instance == null) {
                    instance = new RxEnventBus();
                }
            }
        }
        return instance;
    }

    public void post(Object object) {//通知所有的被观察者
        subject.onNext(object);
    }

    private <T> Observable<T> toObservable(final Class<T> type) {//真正注册被观察者，关联PublishSubject，真正实现订阅
        return subject.ofType(type);
    }

    public boolean hasObservers() {
        return subject.hasObservers();
    }

    public <T> Subscription toSubscription(final Class<T> type, Observer<T> observer) {//注册被观察者和回调方法
        return toObservable(type).subscribe(observer);
    }

    public <T> Subscription toSubscription(final Class<T> type, int subscribeThead,Action1<T> action1) {//注册被观察者和回调方法，指定了回调的线程
        Observable<T> observable=toObservable(type);
        if(subscribeThead==0){
            observable.observeOn(Schedulers.io());
        }else{
            observable.observeOn(AndroidSchedulers.mainThread());
        }
        return observable.subscribe(action1);
    }
    public <T> Subscription toSubscription(final Class<T> type, Action1<T> action1) {//注册被观察者和回调方法,没有指定回调的线程
       ;

        return toObservable(type).subscribe(action1);
    }
}