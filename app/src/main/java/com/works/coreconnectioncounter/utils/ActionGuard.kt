package com.works.coreconnectioncounter.utils

import java.util.concurrent.atomic.AtomicBoolean

/**
 * 連打ガード用の軽量ユーティリティ。
 * - tryAcquire() が true を返した時のみ処理を開始する
 * - 終了時は release() を必ず呼び出す
 */
class ActionGuard {
    private val busy = AtomicBoolean(false)

    fun tryAcquire(): Boolean = busy.compareAndSet(false, true)

    fun release() {
        busy.set(false)
    }
}

