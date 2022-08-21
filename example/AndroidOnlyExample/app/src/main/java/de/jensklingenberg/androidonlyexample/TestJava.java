package de.jensklingenberg.androidonlyexample;

import static de.jensklingenberg.androidonlyexample.MainActivityKt.getApi;
import android.util.Log;

import androidx.annotation.NonNull;

import de.jensklingenberg.ktorfit.Callback;
import io.ktor.client.statement.HttpResponse;

public class TestJava {

    void test() {
        getApi().getPersonCall(1).onExecute(new Callback<Person>() {

            @Override
            public void onError(@NonNull Throwable throwable) {

            }

            @Override
            public void onResponse(Person person, @NonNull HttpResponse httpResponse) {
                Log.d("Android:", person.toString());
            }

        });
    }

}
