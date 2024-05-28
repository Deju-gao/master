package jp.co.rakuten.ticket.checkinstation.util

import com.uber.autodispose.lifecycle.LifecycleScopeProvider
import com.uber.autodispose.lifecycle.autoDisposable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.co.rakuten.ticket.checkinstation.ui.all.allQRStep1.AllQRStep1ViewModel
import jp.co.rakuten.ticket.checkinstation.ui.general.generalQRStep1.GeneralQRStep1ViewModel
import jp.co.rakuten.ticket.checkinstation.ui.menu.readTest.ReadTestViewModel
import jp.co.rakuten.ticket.checkinstation.ui.single.singleQRStep1.SingleQRStep1ViewModel

fun <T> Single<T>.execute(
    lifecycleScopeProvider: LifecycleScopeProvider<*>,
    onSuccess: ((T) -> Unit),
    onError: ((Throwable) -> Unit) = {},
    onFinal: (() -> Unit) = {}
) {
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(onFinal)
        .autoDisposable(lifecycleScopeProvider)
        .subscribe(onSuccess, onError)
}

fun <T> Single<T>.execute(
    lifecycleScopeProvider: LifecycleScopeProvider<*>,
    onSuccess: ((T) -> Unit) = {}
) {
    execute(lifecycleScopeProvider, onSuccess, {})
}

fun <T> Observable<T>.execute(
    lifecycleScopeProvider: LifecycleScopeProvider<*>,
    onNext: ((T) -> Unit),
    onError: ((Throwable) -> Unit) = {},
    onFinal: (() -> Unit) = {}
) {
    subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally(onFinal)
        .autoDisposable(lifecycleScopeProvider)
        .subscribe(onNext, onError)
}

fun <T> Observable<T>.execute(
    lifecycleScopeProvider: ReadTestViewModel,
    onNext: ((T) -> Unit)
) {
    execute(lifecycleScopeProvider, onNext, {})
}

fun <T> Observable<T>.execute(
    lifecycleScopeProvider: AllQRStep1ViewModel,
    onNext: ((T) -> Unit)
) {
    execute(lifecycleScopeProvider, onNext, {})
}

fun <T> Observable<T>.execute(
    lifecycleScopeProvider: GeneralQRStep1ViewModel,
    onNext: ((T) -> Unit)
) {
    execute(lifecycleScopeProvider, onNext, {})
}

fun <T> Observable<T>.execute(
    lifecycleScopeProvider: SingleQRStep1ViewModel,
    onNext: ((T) -> Unit)
) {
    execute(lifecycleScopeProvider, onNext, {})
}
