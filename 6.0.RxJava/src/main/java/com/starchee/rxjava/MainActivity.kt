package com.starchee.rxjava

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()

    companion object {
        private const val STORY_FILE_NAME = "story"
        private const val REGEX_SPLIT = " "
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initStory()
        initSearch()
    }

    private fun initStory() {
        val buffer = StringBuffer()
        try {
            resources.openRawResource(resources.getIdentifier(STORY_FILE_NAME, "raw", packageName))
                .use { inputStream ->
                    inputStream.bufferedReader().forEachLine { buffer.append(it) }
                }
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File $STORY_FILE_NAME not found!", Toast.LENGTH_LONG).show()
        }
        text_view_main.text = buffer.toString()
    }

    private fun initSearch() {
        val searchTextFlowable = Flowable.create<String>({ emitter ->
            search_view_main.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        emitter.onNext(it)
                    }
                    return true
                }
            })

        }, BackpressureStrategy.LATEST)

        val searchDisposable = searchTextFlowable.subscribeOn(Schedulers.io())
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { filter ->
                var count = 0
                Flowable.fromIterable(text_view_main.text.split(REGEX_SPLIT))
                    .map { word ->
                        if (filter.isNotBlank()) {
                            count += (word.count() - word.replace(filter, "", true)
                                .count()) / filter.count()
                            count
                        } else 0
                    }
            }
            .throttleLatest(100, TimeUnit.MILLISECONDS, true)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                count_tv_main.text = it.toString()
            }

        compositeDisposable.add(searchDisposable)

    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
