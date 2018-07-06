package com.tustar.cold

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subscribers.DisposableSubscriber
import java.util.concurrent.TimeUnit

class SplashActivity : AppCompatActivity() {

    private val compositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Welcome_Adaptor_New)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        addSubscription(Flowable.interval(1, TimeUnit.SECONDS)
                .onBackpressureBuffer()
                .take(2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<Long>() {

                    override fun onNext(timer: Long?) {

                    }

                    override fun onComplete() {
                        toMainActivity()
                    }

                    override fun onError(t: Throwable?) {
                        toMainActivity()
                    }
                }))
    }

    private fun toMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {

        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        detachView()
    }

    private fun detachView() {
        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
}
