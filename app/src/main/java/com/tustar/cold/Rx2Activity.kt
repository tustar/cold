package com.tustar.cold

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.tustar.cold.util.Logger
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class Rx2Activity : AppCompatActivity() {

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx)

//        create()
//        map()
//        zip()
//        contact()
//        flatMap()
//        concatMap()
//        distinct()
//        filter()
//        buffer()
//        timer()
//        interval()
//        doOnNext()
//        skip()
//        take()
//        just()
//        single()
//        debounce()
//        defer()
//        last()
//        merge()
//        reduce()
//        scan()
        window()
    }

    override fun onDestroy() {
        super.onDestroy()
        clearDisposable()
    }

    private fun clearDisposable() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
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
        addDisposable(Observable.create(ObservableOnSubscribe<Int> {
            it.onNext(1)
            it.onNext(2)
            it.onNext(3)
        }).map {
            "Result $it"
        }.subscribe {
            Logger.d("$it")
        })
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
        addDisposable(Observable.zip(observableT, observableU,
                BiFunction<String, Int, String> { t1, t2 -> "$t1$t2" })
                .subscribe {
                    Logger.d("$it")
                })
    }

    private fun contact() {
        addDisposable(Observable.concat(Observable.just(1, 2, 3),
                Observable.just("A", "B", "C"))
                .subscribe {
                    Logger.d("$it")
                })
    }

    private fun flatMap() {
        addDisposable(Observable.just(1, 2, 3)
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
                })
    }

    private fun concatMap() {
        addDisposable(Observable.just(1, 2, 3)
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
                })
    }

    private fun distinct() {
        addDisposable(Observable.just(1, 2, 3, 1, 3, 1, 3, 2, 4)
                .distinct()
                .subscribe {
                    Logger.e("$it")
                })
    }

    private fun filter() {
        addDisposable(Observable.just(1, 2, 3, 4, 6, 7, 9)
                .filter {
                    it % 2 == 0
                }.subscribe {
                    Logger.w("$it")
                })
    }

    private fun buffer() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .buffer(3, 2)
                .subscribe {
                    Logger.d("size = ${it.size}")
                    it.forEach {
                        Logger.d("$it")
                    }
                })
    }

    private fun timer() {
        addDisposable(Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun interval() {
        addDisposable(Observable.interval(3, 2,
                TimeUnit.SECONDS)
                .take(8)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun doOnNext() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .doOnNext {
                    Logger.d("doOnNext :: $it")
                }
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun skip() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .skip(2)
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun take() {
        addDisposable(Flowable.fromArray(1, 2, 3, 4, 5)
                .take(2)
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun just() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun single() {
        Single.just(Random().nextInt())
                .subscribe(object : SingleObserver<Int> {
                    override fun onSuccess(t: Int) {
                        Logger.d("$t")
                    }

                    override fun onSubscribe(d: Disposable) {
                        addDisposable(d)
                        Logger.d("$d")
                    }

                    override fun onError(e: Throwable) {
                        Logger.d("$e")
                    }
                })
    }

    private fun debounce() {
        addDisposable(Observable
                .create<Int> {
                    it.onNext(1) // skip
                    Thread.sleep(400)
                    it.onNext(2) // deliver
                    Thread.sleep(505)
                    it.onNext(3) // skip
                    Thread.sleep(100)
                    it.onNext(4) // deliver
                    Thread.sleep(605)
                    it.onNext(5) // deliver
                    Thread.sleep(510)
                    it.onComplete()
                }
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun defer() {
        addDisposable(Observable.defer {
            Observable.just(1, 2, 3)
        }.subscribe {
            Logger.d("subscribe :: $it")
        })
    }

    private fun last() {
        addDisposable(Observable.just(1, 2, 3)
                .last(4)
                .subscribe { it ->
                    Logger.d("subscribe :: $it")
                })
    }

    private fun merge() {
        addDisposable(Observable.merge(
                Observable.just(1, 2),
                Observable.just(3, 4, 5))
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun reduce() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .reduce { t1: Int, t2: Int -> t1 + t2 }
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun scan() {
        addDisposable(Observable.just(1, 2, 3, 4, 5)
                .scan { t1: Int, t2: Int -> t1 + t2 }
                .subscribe {
                    Logger.d("subscribe :: $it")
                })
    }

    private fun window() {
        addDisposable(Observable.interval(1, TimeUnit.SECONDS)
                .take(15)
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Logger.d("Sub Divide begin...")
                    it.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                Logger.d("subscribe :: $it")
                            }
                })
    }
}
