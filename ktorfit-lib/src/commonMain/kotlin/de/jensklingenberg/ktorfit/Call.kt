package de.jensklingenberg.ktorfit

interface Call<T> {
    fun onExecute(callBack: Callback<T>)
}
