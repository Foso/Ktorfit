package de.jensklingenberg.androidonlyexample;

import static de.jensklingenberg.androidonlyexample.MainActivityKt.testApi;

import android.util.Log;

import androidx.annotation.NonNull;

import de.jensklingenberg.ktorfit.Callback;
import io.ktor.client.statement.HttpResponse;

public class TestJava {

    void test(){
        testApi().getPersonCall().onExecute(new Callback<String>() {
            @Override
            public void onResponse(String s, @NonNull HttpResponse httpResponse) {
                Log.d("Android:",s);
            }

            @Override
            public void onError(@NonNull Exception e) {

            }
        });
    }

}
