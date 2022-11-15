package com.fc.todolist.livedata
import androidx.lifecycle.Observer

class LiveDataTestObserver<T> : Observer<T> {

    private val values: MutableList<T> = mutableListOf()

    // 값이 바뀔 때 마다 내부의 배열에 계속 저장
    override fun onChanged(t: T) {
        values.add(t)
    }

    // 실제 값 예상 값 테스트
    fun assertValueSequence(sequence: List<T>): LiveDataTestObserver<T> {
        var i = 0
        // 실제 값, 예상 값을 계속 비교하기 위해 iterator를 만듦
        val actualIterator = values.iterator()
        val expectedIterator = sequence.iterator()

        var actualNext: Boolean
        var expectedNext: Boolean

        // 실제, 예상 값 리스트 비교
        while (true) {
            actualNext = actualIterator.hasNext()
            expectedNext = expectedIterator.hasNext()

            // 둘 다 바뀐 게 없으면 break
            if (!actualNext || !expectedNext) break


            // 비교 시작
            val actual: T = actualIterator.next()
            val expected: T = expectedIterator.next()


            // 다르면 에러 발생
            if (actual != expected) {
                throw AssertionError("actual: ${actual}, expected: ${expected}, index: $i")
            }

            // 같으면 계속 순회
            i++
        }

        // break시 상태를 보고 에러 출력
//        if (actualNext) {
//            throw AssertionError("More values received than expected ($i)")
//        }
//        if (expectedNext) {
//            throw AssertionError("Fewer values received than expected ($i)")
//        }

        return this
    }
}