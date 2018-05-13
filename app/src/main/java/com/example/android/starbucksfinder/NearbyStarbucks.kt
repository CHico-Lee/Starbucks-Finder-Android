package com.example.android.starbucksfinder

/**
 * Created by leech on 5/7/2018.
 */

public class NearbyStarbucks private constructor() {
    init {
    }

    private object Holder {
        val INSTANCE = NearbyStarbucks()
    }

    companion object {
        val instance: NearbyStarbucks by lazy { Holder.INSTANCE }
    }

    var storeList = arrayListOf<Store>()
}