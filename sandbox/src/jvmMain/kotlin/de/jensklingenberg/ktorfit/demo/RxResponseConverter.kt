package de.jensklingenberg.ktorfit.demo

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverter
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RxResponseConverter : ResponseConverter {

    override fun supportedType(returnTypeName: String, isSuspend: Boolean): Boolean {
        return listOf("io.reactivex.rxjava3.core.Single", "io.reactivex.rxjava3.core.Observable","io.reactivex.rxjava3.core.Completable").contains(
            returnTypeName
        )
    }

    override fun <PRequest> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {

        return when (returnTypeName) {
            "io.reactivex.rxjava3.core.Single" -> {
                Single.create<PRequest> { e ->

                    try {
                        GlobalScope.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response.call.body(info) as PRequest
                                }catch (ex: Exception){
                                    e.onError(ex)
                                    null
                                }
                            }
                            val success = result.await()
                            success?.let { e.onSuccess(it) }
                        }
                    } catch (ex: NumberFormatException) {
                        println("dfsdfsdfsdfds")
                        e.onError(ex)
                    }

                }
            }
            "io.reactivex.rxjava3.core.Observable" -> {
                Observable.create<PRequest> { e ->
                    try {
                        GlobalScope.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response.call.body(info) as PRequest
                                }catch (ex: Exception){
                                    e.onError(ex)
                                    null
                                }
                            }
                            val success = result.await()
                            success?.let {
                                e.onNext(it)
                                e.onComplete()
                            }
                        }
                    } catch (ex: NumberFormatException) {
                        println("dfsdfsdfsdfds")
                        e.onError(ex)
                    }
                }
            }

            "io.reactivex.rxjava3.core.Completable" -> {
                Completable.create { e ->
                    try {
                        GlobalScope.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response
                                }catch (ex: Exception){
                                    e.onError(ex)
                                    null
                                }
                            }
                            val success = result.await()
                            success?.let {
                                e.onComplete()
                            }
                        }
                    } catch (ex: NumberFormatException) {
                        e.onError(ex)
                    }
                }
            }
            else -> {
                throw NullPointerException()
            }
        }


    }

}
