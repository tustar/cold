package com.tustar.cold

import android.os.Bundle
import android.support.v4.util.LogWriter
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.tustar.cold.util.Logger
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        create()
        map()
        zip()
        contact()
        flatMap()
        concatMap()
        distinct()
        filter()
    }

    private fun create() {
        Observable.create(ObservableOnSubscribe<Int> {
            Logger.d("Observable emit 1")
            it.onNext(1)
            Logger.d("Observable emit 2")
            it.onNext(2)
            Logger.d("Observable emit 3")
            it.onNext(3)
            it.onComplete()
            Logger.d("Observable emit 4")
            it.onNext(4)
        }).subscribe(object : Observer<Int> {
            private var i: Int = 0
            private lateinit var disposable: Disposable

            override fun onSubscribe(d: Disposable) {
                Logger.d("${d.isDisposed}")
                disposable = d
            }

            override fun onNext(t: Int) {
                Logger.d("$t")
                i++
                if (i == 2) {
                    disposable.dispose()
                    Logger.d("isDisposed : ${disposable.isDisposed}")
                }

            }

            override fun onComplete() {
                Logger.d()
            }


            override fun onError(e: Throwable) {
                Logger.e(e.message)
            }
        })
    }

    private fun map() {
        Observable.create(ObservableOnSubscribe<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onNext(3)
        }).map {
            "Result $it"
        }.subscribe {
            Logger.d("$it")
        }
    }

    private fun zip() {
        val observableT = Observable.create<String> {
            if (!it.isDisposed) {
                Logger.d("String emit A")
                it.onNext("A")
                Logger.d("String emit B")
                it.onNext("B")
                Logger.d("String emit C")
                it.onNext("C")
            }
        }
        val observableU = Observable.create<Int> {
            if (!it.isDisposed) {
                Logger.d("Int emit 1")
                it.onNext(1)
                Logger.d("Int emit 2")
                it.onNext(2)
                Logger.d("Int emit 3")
                it.onNext(3)
                Logger.d("Int emit 4")
                it.onNext(4)
                Logger.d("Int emit 5")
                it.onNext(5)
            }
        }
        Observable.zip(observableT, observableU,
                BiFunction<String, Int, String> { t1, t2 -> "$t1$t2" })
                .subscribe {
                    Logger.d("$it")
                }
    }

    private fun contact() {
        Observable.concat(Observable.just(1, 2, 3),
                Observable.just("A", "B", "C"))
                .subscribe {
                    Logger.d("$it")
                }
    }

    private fun flatMap() {
        Observable.just(1, 2, 3)
                .flatMap { integer ->
                    val list = mutableListOf<String>()
                    (0..2).forEach {
                        list.add("I am value $integer")
                    }
                    val delayTime = (1 + Math.random() * 10L).toLong()
                    Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS)
                }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("$it")
                }
    }

    private fun concatMap() {
        Observable.just(1, 2, 3)
                .concatMap { integer ->
                    val list = mutableListOf<String>()
                    (0..2).forEach {
                        list.add("I am value $integer")
                    }
                    val delayTime = (1 + Math.random() * 10L).toLong()
                    Observable.fromIterable(list).delay(delayTime, TimeUnit.MILLISECONDS)
                }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("$it")
                }
    }

    private fun distinct() {
        Observable.just(1, 2, 3, 1, 3, 1, 3, 2, 4)
                .distinct()
                .subscribe {
                    Logger.e("$it")
                }
    }

    private fun filter() {
        Observable.just(1, 2, 3, 4, 6, 7, 9)
                .filter {
                    it % 2 == 0
                }.subscribe {
                    Logger.w("$it")
                }
    }
}
