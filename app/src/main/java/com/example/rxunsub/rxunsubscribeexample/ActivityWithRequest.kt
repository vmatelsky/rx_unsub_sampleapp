package com.example.rxunsub.rxunsubscribeexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_with_request.*
import org.jetbrains.anko.toast
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers.io
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit

class ActivityWithRequest : AppCompatActivity() {

    val subs = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_with_request)

        btnStartRequest.setOnClickListener {
            subs.add(
                    firstPartOfRequest()
                            .flatMap(this::secondPartOfRequest)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    { logAndShowToast("Completed") },
                                    Throwable::printStackTrace
                            )
            )
        }
    }

    override fun onStop() {
        super.onStop()

        subs.clear()
    }

    fun firstPartOfRequest(): Single<String> = Single.fromCallable {

        // simulate some request that takes 2 seconds
        TimeUnit.SECONDS.sleep(2)

        "result"   // result of request 1
    }
            .doOnSubscribe { logAndShowToast("Subscribed to first") }
            .doOnUnsubscribe { logAndShowToast("Unsubscribed from first") }
            .doOnSuccess{ logAndShowToast("Result of first: $it") }
            .subscribeOn(io())

    fun secondPartOfRequest(param: String): Single<String> = Single.fromCallable {

        // simulate some request that takes 2 seconds
        TimeUnit.SECONDS.sleep(2)

        "$param 2" // result of request 2
    }
            .doOnSubscribe { logAndShowToast("Subscribed to second") }
            .doOnUnsubscribe { logAndShowToast("Unsubscribed from second") }
            .doOnSuccess{ logAndShowToast("Result of second: $it") }
            .subscribeOn(io())

    fun logAndShowToast(text: String) = btnStartRequest.post {
        toast(text)
        Log.d("DEBUGGING", text)
    }
}
