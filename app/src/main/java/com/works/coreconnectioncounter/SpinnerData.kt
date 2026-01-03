package com.works.coreconnectioncounter

object SpinnerData {

    // ========== タイトル（共通） ==========
    const val PILOT_TITLE = "パイロット"
    const val MECHA_TITLE = "機体"

    // ========== エクエス用データ ==========
    val EQUES_PILOT_ITEMS = arrayOf(
        "選抜パイロット",
        "日野アカリ",
        "エミリア・ホーク",
        "ロマーナ・イリス",
        "クレオ&パトラ・エルハーム",
        "ガオ・ファン",
        "ラーシャ・ドラグノフ"
    )

    val EQUES_PILOT_VALUES = arrayOf(
        intArrayOf(1, 0, 1),      // 選抜パイロット 101（初期値）
        intArrayOf(4, 0, 4),      // 日野アカリ 404
        intArrayOf(2, 1, 2),      // エミリア・ホーク 212
        intArrayOf(0, 3, 3),      // ロマーナ・イリス 033
        intArrayOf(0, 0, 2),      // クレオ&パトラ・エルハーム 002
        intArrayOf(2, 0, 4),      // ガオ・ファン 204
        intArrayOf(0, 0, 5)       // ラーシャ・ドラグノフ 005
    )

    val EQUES_PILOT_BOOST_VALUES = arrayOf(
        intArrayOf(0, 0, 0),      // 選抜パイロット
        intArrayOf(6, 0, 6),      // 日野アカリ 606
        intArrayOf(3, 2, 3),      // エミリア・ホーク 323
        intArrayOf(0, 5, 5),      // ロマーナ・イリス 055
        intArrayOf(2, 2, 4),      // クレオ&パトラ・エルハーム 224
        intArrayOf(4, 0, 6),      // ガオ・ファン 406
        intArrayOf(5, 5, 0)       // ラーシャ・ドラグノフ 550
    )


    val EQUES_MECHA_ITEMS = arrayOf(
        "量産機",
        "カンナガラ三號",
        "SUPERNOVA",
        "Er-28++ ケリュケイオン",
        "マフデト",
        "盤古",
        "トリグラフ"
    )

    val EQUES_MECHA_VALUES = arrayOf(
        intArrayOf(1, 0, 1),      // 量産機 101
        intArrayOf(8, 0, 8),      // カンナガラ三號 808
        intArrayOf(6, 4, 6),      // SUPERNOVA 646
        intArrayOf(0, 6, 9),      // Er-28++ ケリュケイオン 069
        intArrayOf(1, 1, 3),      // マフデト 113
        intArrayOf(2, 0, 6),      // 盤古 206
        intArrayOf(7, 5, 3)       // トリグラフ 753
    )

    val EQUES_MECHA_BOOST_VALUES = arrayOf(
        intArrayOf(0, 0, 0),      // 量産機
        intArrayOf(8, 0, 8),      // カンナガラ三號 808
        intArrayOf(6, 4, 6),      // SUPERNOVA 646
        intArrayOf(0, 6, 9),      // Er-28++ ケリュケイオン 069
        intArrayOf(1, 1, 3),      // マフデト 113
        intArrayOf(2, 0, 6),      // 盤古 206
        intArrayOf(7, 5, 3)       // トリグラフ 753
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
        return if (mode == "mode2") MODE2_SPINNER1_ITEMS else EQUES_PILOT_ITEMS
    }

    fun getSpinner1Values(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER1_VALUES else EQUES_PILOT_VALUES
    }

    fun getSpinner1BoostValues(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER1_BOOST_VALUES else EQUES_PILOT_BOOST_VALUES
    }

    fun getSpinner2Items(mode: String): Array<String> {
        return if (mode == "mode2") MODE2_SPINNER2_ITEMS else EQUES_MECHA_ITEMS
    }

    fun getSpinner2Values(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER2_VALUES else EQUES_MECHA_VALUES
    }

    fun getSpinner2BoostValues(mode: String): Array<IntArray> {
        return if (mode == "mode2") MODE2_SPINNER2_BOOST_VALUES else EQUES_MECHA_BOOST_VALUES
    }
}
