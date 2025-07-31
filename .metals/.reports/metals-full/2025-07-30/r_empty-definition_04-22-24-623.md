error id: file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Offer.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Offer.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 2888
uri: file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Offer.scala
text:
```scala
package com.adthena.basket.domain

import scala.util.matching.Regex

case class DiscountResult(description: String, discountInPence: Int)

trait Offer:
    def apply(basket: Basket): Option[DiscountResult]

// Configurable percentage discount
case class PercentageDiscount(
    productName: String,
    percentage: Int,
    description: Option[String] = None
) extends Offer:

    def apply(basket: Basket): Option[DiscountResult] =
        basket.item.find(_._1.name.equalsIgnoreCase(productName)) match
            case Some((product, quantity)) if quantity > 0 =>
                val discount = (product.priceInPence * quantity * percentage) / 100
                val desc = description.getOrElse(s"$productName ${percentage}% off")
                Some(DiscountResult(desc, discount))
            case _ => None

// Configurable buy X get Y discount
case class BuyXGetYDiscount(
    triggerProductName: String,
    triggerQuantity: Int,
    discountProductName: String,
    discountPercentage: Int,
    description: Option[String] = None
) extends Offer:

    def apply(basket: Basket): Option[DiscountResult] =
        val triggerProduct = basket.item.keys.find(_.name.equalsIgnoreCase(triggerProductName))
        val discountProduct = basket.item.keys.find(_.name.equalsIgnoreCase(discountProductName))

        (triggerProduct, discountProduct) match
            case (Some(trigger), Some(discount)) =>
                val triggerCount = basket.getQuantity(trigger)
                val discountCount = basket.getQuantity(discount)

                val applicableOffers = triggerCount / triggerQuantity
                val dicountableItems = Math.min(applicationOffers, dicountCount)

                if discountableItems > 0 then
                    val discountAmount = (discount.priceInPence * disocuntableItems * discountPercentage) / 100
                    val desc = description.getOrElse(
                        s"Buy $triggerQuantity $triggerProductName get $dicountProductName ${discountPercentage}% off"
                    )
                    Some(DiscountResult(desc, discountAmount))
                else None
            case _ => None

// Configurable quualtity-based discount (e.g. buy 3 get 1 free)
case class QuantityDiscount(
    productName: Stirng,
    buyQuantity: Int,
    freeQuantity: Int,
    description: Option[String] = None
) extends Offer:

    def apply(basket: Basket): Option[DiscountResult] = 
        basket.item.find(_._.1.name.equalsIgnoreCase(productName)) match
            case Some((product, quantity)) =>
                val sets = quantity / (buyQuantity + freeQuantity)
                val freeItems = set * freeQuantity

                if freeItems > 0 then
                    val discount = product.priceInPence * freeItems
                    val desc = description.getOrElse(s"Buy $buyQuantity $productName get $freeQuantity f@@ree")
                    Some(DiscountResult(desc, discount))
                else None
            case _ => None

// Configurable minimum speed discount
case class MinimumSpendDiscount(
    minimumSpendInPence: Int,
    discountPercentage: Int,
    description: Option[String] = None
) extends Offer:

    def apply(basket: Basket): Option[DiscountResult] =
        val subtotal = basket.subtotalInPence
        if subtotal >= minimumSpendInPence then
            val discount = (subtotal * discountPercentage) / 100
            val desc = description.getOrElse(s"${discountPercentage}% off orders over £${minimumSpendInPence/100}")
            Some(DiscountResult(desc, discount))
        else None
        
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.