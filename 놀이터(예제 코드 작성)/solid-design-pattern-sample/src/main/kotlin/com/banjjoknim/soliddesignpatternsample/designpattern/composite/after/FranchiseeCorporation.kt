package com.banjjoknim.soliddesignpatternsample.designpattern.composite.after

class FranchiseeCorporation(
    private val name: String,
    private val stores: MutableList<Store> = mutableListOf()
) : Store {
    fun addStore(store: Store) {
        stores.add(store)
    }

    fun removeStore(store: Store) {
        stores.remove(store)
    }

    override fun calculate() {
        for (store in stores) {
            store.calculate()
        }
        println("프랜차이즈 기업 [$name]의 정산을 완료했습니다.")
    }
}
