import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.File
import java.io.FileNotFoundException

/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

fun main(args: Array<String>) {

    exampleOf("just") {
        Observable.fromIterable(listOf(1, 2, 3)).subscribe { next -> println(next) }
    }

    exampleOf("empty") {
        Observable.empty<Unit>().subscribeBy(
            onNext = { next -> println(next) },
            onComplete = { println("complete") },
            onError = { println("error") }
        )
    }

    exampleOf("never") {

        val disposables = CompositeDisposable()

        val observable = Observable.never<Any>()
            .doOnNext { println(it) }
            .doOnComplete { println("Completed") }
            .doOnSubscribe { println("Subscribed") }
            .doOnDispose { println("Disposed") }
            .subscribeBy(onNext = {
                println(it)
            }, onComplete = {
                println("Completed")
            })

        disposables.add(observable)
        disposables.dispose()
    }

    exampleOf("range") {
        Observable.range(1, 10).subscribeBy(
            onNext = { println(it) },
            onComplete = { println("completed") },
            onError = { println("error") }
        )
    }

    exampleOf("dispose") {
        val observable = Observable.just(1, 2, 3)
        val subscription = observable.subscribe { println(it) }
        subscription.dispose()
    }

    exampleOf("compositeDisposable") {
        val subscriptions = CompositeDisposable()

        val disposable = Observable.just("A", "B", "C").subscribe {
            println(it)
        }

        subscriptions.addAll(disposable)
        subscriptions.dispose()
    }

    exampleOf("create") {
        val disposables = CompositeDisposable()

        val observable = Observable.create<String> { emitter ->
            emitter.onNext("1")
            emitter.onNext("?")
            emitter.onError(RuntimeException("Error"))
            emitter.onComplete()
        }.subscribeBy(
            onNext = { println(it) },
            onComplete = { println("complete") },
            onError = { println("Error") }
        )
        disposables.add(observable)
        disposables.dispose()
    }

    exampleOf("factory") {

        val disposables = CompositeDisposable()

        var flip = false
        val factory = Observable.defer {
            flip = !flip
            if (flip) {
                Observable.just(1, 2, 3)
            } else {
                Observable.just(4, 5, 6)
            }
        }

        for (i in 0..3) {
            disposables.add(
                factory.subscribe {
                    println(it)
                }
            )
        }
        disposables.dispose()
    }

    exampleOf("Single") {
        val subscriptions = CompositeDisposable()

        fun loadText(filename: String): Single<String> {
            return Single.create create@{ emitter ->
                val file = File(filename)
                if (!file.exists()) {
                    emitter.onError(FileNotFoundException("Can???t find $filename"))
                    return@create
                }
                val contents = file.readText(Charsets.UTF_8)
                emitter.onSuccess(contents)
            }
        }

        val observer = loadText("Copyright.txt").subscribeBy(
            onSuccess = { println(it) },
            onError = { println("Error , $it") }
        )
        subscriptions.add(observer)
    }

}