package com.akari.tickets.utils;

import rx.Subscription;

/**
 * Created by Akari on 2017/2/17.
 */

public class SubscriptionUtil {
    public static void unSubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
