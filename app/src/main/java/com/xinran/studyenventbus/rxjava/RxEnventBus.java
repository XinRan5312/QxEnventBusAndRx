package com.xinran.studyenventbus.rxjava;

import rx.Observer;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * Created by qixinh on 16/3/31.
 */
public class RxEnventBus {


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

    public <T> Subscription toSubscription(final Class<T> type, Action1<T> action1) {//注册被观察者和回调方法
        return toObservable(type).subscribe(action1);
    }
}