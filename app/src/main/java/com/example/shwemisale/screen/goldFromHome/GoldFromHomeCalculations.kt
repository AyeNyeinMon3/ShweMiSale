package com.example.shwemisale.screen.goldFromHome

import kotlin.math.roundToInt

fun getKyatsFromKPY(kyat: Int, pae: Int, ywae: Double): Double {
    return kyat.toDouble() + pae.toDouble() / 16 + ywae / 128
}

fun getYwaeFromKPY(kyat: Int, pae: Int, ywae: Double): Double {
    return ((kyat.toDouble() * 128 + pae.toDouble() * 8 + ywae)*100).roundToInt() / 100.0
}

fun getYwaeFromGram(gram: Double): Double {
    return (((gram / 16.6) * 128.toDouble())*100).roundToInt() / 100.0
}

fun getGramFromYwae(ywae:Double):Double{
    return (((ywae/128)*16.6)*100).roundToInt() / 100.0
}

fun getKPYFromKyat(kyat: Double): List<Double> {
    val resultList = mutableListOf<Double>()
    var kpy_kyat = kyat.toInt()
    var decimal_pae = (kyat - kpy_kyat) * 16
    var kpy_pae = decimal_pae.toInt()
    var kpy_ywae = (decimal_pae - kpy_pae) * 8;

    resultList.add(kpy_kyat.toDouble())
    resultList.add(kpy_pae.toDouble())
    resultList.add(kpy_ywae)
    return resultList
}

fun getKPYFromYwae(ywae: Double): List<Double> {
    val resultList = mutableListOf<Double>()
    var pae = (ywae / 8).toInt()
    var resultYwae = ywae % 8

    var kyat = (pae / 16)
    pae = pae % 16
    resultList.add((kyat.toDouble()*100).roundToInt() / 100.0)
    resultList.add((pae.toDouble()*100).roundToInt() / 100.0)
    resultList.add((resultYwae*100).roundToInt() / 100.0)
    return resultList
}

fun main() {
    println(getKPYFromYwae(211.1))
}

fun getGoldWeight(goldGemWeight: Double, gemWeight: Double, impurityWeight: Double): Double {
    return goldGemWeight - gemWeight - impurityWeight
}

//Rebuy price= GQ in carat /24 *100% price
fun getRebuyPriceWithGQ(gqCarat: Double, oneHundredPercentPrice: Double): String {
    return String.format("%.2f", gqCarat / 24 * oneHundredPercentPrice)
}

fun getOneHundredPercentPriceWithGQ(gqCarat: Double, rebuyPrice: Double): String {
    return String.format("%.2f", rebuyPrice * 24 / gqCarat)
}

fun getBuyPriceFromShop(
    goldWeightYwae: Double,
    reducedYwae: Double,
    rebuyPrice: Double,
    otherReducedCost: Double
): String {
    val kpy = getKPYFromYwae(goldWeightYwae + reducedYwae)
    val kyat = getKyatsFromKPY(kpy[0].toInt(), kpy[1].toInt(), kpy[2])

    val result = kyat * rebuyPrice + otherReducedCost

    return String.format("%.2f", result)
}

fun getDecidedPawnprice(rebuyPrice: Double, pawnDiffValue: Double): String {
    return String.format("%.2f", rebuyPrice - pawnDiffValue)
}

fun getPawnPrice(
    goldWeightYwae: Double, reducedYwae: Double, decidedPawnPrice: Double,
    otherReducedCost: Double
): String {
    val kpy = getKPYFromYwae(goldWeightYwae + reducedYwae)
    val kyat = getKyatsFromKPY(kpy[0].toInt(), kpy[1].toInt(), kpy[2])

    val result = kyat * decidedPawnPrice + otherReducedCost
    return String.format("%.2f", result)
}

fun geta(
    b: Double,
    makeCost: Double,
    gemValue: Double,
    ptCost: Double,
    goldWeightYwae: Double,
    reducedYwae: Double
): String {
    val kpy = getKPYFromYwae(goldWeightYwae + reducedYwae)
    val kyat = getKyatsFromKPY(kpy[0].toInt(), kpy[1].toInt(), kpy[2])
    val result = (b-makeCost-gemValue-ptCost)/kyat
    return String.format("%.2f", result)

}

fun getd(
    b:Double,
    makeCost: Double,
    gemValue: Double,
    ptCost: Double,
    c:Double,
    reducedYwae: Double
):String{
    val result = (b-makeCost-gemValue-ptCost)/c -reducedYwae
    return String.format("%.2f", result)
}