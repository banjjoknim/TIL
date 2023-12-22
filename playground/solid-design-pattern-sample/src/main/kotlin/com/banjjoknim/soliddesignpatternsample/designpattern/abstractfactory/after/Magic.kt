package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.after

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.FruitType

interface Magic {
    companion object {
        fun getMagic(fruitType: FruitType): Magic {
            return when (fruitType) {
                FruitType.APPLE -> AppleMagic()
                FruitType.BANANA -> BananaMagic()
                FruitType.ORANGE -> OrangeMagic()
            }
        }
    }

    fun createFruit()

    fun createJuice()
}

//abstract class Magic {
//    companion object {
//        fun getMagic(fruitType: FruitType): Magic {
//            return when (fruitType) {
//                FruitType.APPLE -> AppleMagic()
//                FruitType.BANANA -> BananaMagic()
//                FruitType.ORANGE -> OrangeMagic()
//            }
//        }
//    }
//
//    abstract fun createFruit()
//
//    abstract fun createJuice()
//}
