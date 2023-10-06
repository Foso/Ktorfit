package de.jensklingenberg.ktorfit

public interface Call<T> {
    public fun onExecute(callBack: Callback<T>)
}
