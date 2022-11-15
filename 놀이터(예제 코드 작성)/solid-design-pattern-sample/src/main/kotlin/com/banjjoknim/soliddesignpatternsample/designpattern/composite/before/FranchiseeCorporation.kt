package com.banjjoknim.soliddesignpatternsample.designpattern.composite.before

class FranchiseeCorporation(
    private val name: String,
    private val corporations: MutableList<FranchiseeCorporation> = mutableListOf(),
    private val stores: MutableList<FranchiseeStore> = mutableListOf()
) {
    fun addCorporation(franchiseeCorporation: FranchiseeCorporation) {
        corporations.add(franchiseeCorporation)
    }

    fun removeCorporation(franchiseeCorporation: FranchiseeCorporation) {
        corporations.remove(franchiseeCorporation)
    }

    fun addStore(store: FranchiseeStore) {
        stores.add(store)
    }

    fun removeStore(store: FranchiseeStore) {
        stores.remove(store)
    }

    fun calculateAllCorporations() {
        for (corporation in corporations) {
            corporation.calculateAllStores()
        }
        println("프랜차이즈 기업 [$name]의 정산을 완료했습니다.")
    }

    fun calculateAllStores() {
        for (store in stores) {
            store.calculate()
        }
        println("프랜차이즈 기업 [$name]의 정산을 완료했습니다.")
    }
}
