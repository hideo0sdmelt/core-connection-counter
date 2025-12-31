package com.works.coreconnectioncounter

object SpinnerData {

    // ========== タイトル（共通） ==========
    const val SPINNER1_TITLE = "カテゴリ1"
    const val SPINNER2_TITLE = "カテゴリ2"

    // ========== Mode1用データ ==========
    val MODE1_SPINNER1_ITEMS = arrayOf("オプション1", "オプション2", "オプション3")

    val MODE1_SPINNER1_VALUES = arrayOf(
        intArrayOf(5, 3, 1),
        intArrayOf(10, 6, 2),
        intArrayOf(15, 9, 3)
    )

    val MODE1_SPINNER1_BOOST_VALUES = arrayOf(
        intArrayOf(5, 3, 1),
        intArrayOf(10, 6, 2),
        intArrayOf(15, 9, 3)
    )

    val MODE1_SPINNER2_ITEMS = arrayOf("選択A", "選択B", "選択C")

    val MODE1_SPINNER2_VALUES = arrayOf(
        intArrayOf(1, 1, 1),
        intArrayOf(2, 2, 2),
        intArrayOf(3, 3, 3)
    )

    val MODE1_SPINNER2_BOOST_VALUES = arrayOf(
        intArrayOf(1, 1, 1),
        intArrayOf(2, 2, 2),
        intArrayOf(3, 3, 3)
    )

    // ========== Mode2用データ ==========
    val MODE2_SPINNER1_ITEMS = arrayOf("タイプA", "タイプB", "タイプC")

    val MODE2_SPINNER1_VALUES = arrayOf(
        intArrayOf(10, 5, 2),
        intArrayOf(20, 10, 4),
        intArrayOf(30, 15, 6)
    )

    val MODE2_SPINNER1_BOOST_VALUES = arrayOf(
        intArrayOf(10, 5, 2),
        intArrayOf(20, 10, 4),
        intArrayOf(30, 15, 6)
    )

    val MODE2_SPINNER2_ITEMS = arrayOf("種類X", "種類Y", "種類Z")

    val MODE2_SPINNER2_VALUES = arrayOf(
        intArrayOf(5, 5, 5),
        intArrayOf(10, 10, 10),
        intArrayOf(15, 15, 15)
    )

    val MODE2_SPINNER2_BOOST_VALUES = arrayOf(
        intArrayOf(5, 5, 5),
        intArrayOf(10, 10, 10),
        intArrayOf(15, 15, 15)
    )

    // ========== ボタン ==========
    const val BOOST1_BUTTON_TITLE = "覚醒"
    const val BOOST2_BUTTON_TITLE = "臨界"

    // ========== カウンター ==========
    val COUNTER_TITLES = arrayOf(
        "近接",
        "狙撃",
        "耐久",
        "翡翠",
        "決意",
        "EP"
    )

    // ========== ヘルパー関数 ==========

    fun getSpinner1Items(mode: String): Array<String> {
        return if (mode == "mode2") MODE2_SPINNER1_ITEMS else MODE1_SPINNER1_ITEMS
    }

    fun getSpinner1Values(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER1_VALUES else MODE1_SPINNER1_VALUES
    }

    fun getSpinner1BoostValues(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER1_BOOST_VALUES else MODE1_SPINNER1_BOOST_VALUES
    }
    
    fun getSpinner2Items(mode: String): Array<String> {
        return if (mode == "mode2") MODE2_SPINNER2_ITEMS else MODE1_SPINNER2_ITEMS
    }

    fun getSpinner2Values(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER2_VALUES else MODE1_SPINNER2_VALUES
    }

    fun getSpinner2BoostValues(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER2_BOOST_VALUES else MODE1_SPINNER2_BOOST_VALUES
    }
}
