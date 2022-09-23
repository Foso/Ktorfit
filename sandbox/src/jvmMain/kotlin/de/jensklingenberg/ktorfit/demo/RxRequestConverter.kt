package de.jensklingenberg.ktorfit.demo

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.request.RequestConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RxRequestConverter : RequestConverter {

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return listOf("io.reactivex.rxjava3.core.Single", "io.reactivex.rxjava3.core.Observable","io.reactivex.rxjava3.core.Completable").contains(
            typeData.qualifiedName
        )
    }

    override fun <RequestType> convertRequest(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse?>,
        ktorfit: Ktorfit
    ): Any {
        return when (typeData.qualifiedName) {
            "io.reactivex.rxjava3.core.Single" -> {
                Single.create<RequestType> { e ->

                    try {
                        ktorfit.httpClient.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response!!.body(info) as RequestType
                                }catch (ex: Exception){
                                    e.onError(ex)
                                    null
                                }
                            }
                            val success = result.await()
                            success?.let { e.onSuccess(it) }
                        }
                    } catch (ex: Exception) {
                        e.onError(ex)
                    }

                }
            }
            "io.reactivex.rxjava3.core.Observable" -> {
                Observable.create<RequestType> { e ->
                    try {
                        ktorfit.httpClient.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response!!.body(info) as RequestType
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
                    } catch (ex: Exception) {
                        e.onError(ex)
                    }
                }
            }

            "io.reactivex.rxjava3.core.Completable" -> {
                Completable.create { e ->
                    try {
                        ktorfit.httpClient.launch {
                            val result = async {
                                try {
                                    val (info, response) = requestFunction()
                                    response!!.body(info) as RequestType
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
                    } catch (ex: Exception) {
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
