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
    basket.items.find(_._1.name.equalsIgnoreCase(productName)) match
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
  discountPercentage: Int, // 50 for half price, 100 for free
  description: Option[String] = None
) extends Offer:
  
  def apply(basket: Basket): Option[DiscountResult] =
    val triggerProduct = basket.items.keys.find(_.name.equalsIgnoreCase(triggerProductName))
    val discountProduct = basket.items.keys.find(_.name.equalsIgnoreCase(discountProductName))
    
    (triggerProduct, discountProduct) match
      case (Some(trigger), Some(discount)) =>
        val triggerCount = basket.getQuantity(trigger)
        val discountCount = basket.getQuantity(discount)
        
        val applicableOffers = triggerCount / triggerQuantity
        val discountableItems = Math.min(applicableOffers, discountCount)
        
        if discountableItems > 0 then
          val discountAmount = (discount.priceInPence * discountableItems * discountPercentage) / 100
          val desc = description.getOrElse(
            s"Buy $triggerQuantity $triggerProductName get $discountProductName ${discountPercentage}% off"
          )
          Some(DiscountResult(desc, discountAmount))
        else None
      case _ => None

// Configurable quantity-based discount (e.g., buy 3 get 1 free)
case class QuantityDiscount(
  productName: String,
  buyQuantity: Int,
  freeQuantity: Int,
  description: Option[String] = None
) extends Offer:
  
  def apply(basket: Basket): Option[DiscountResult] =
    basket.items.find(_._1.name.equalsIgnoreCase(productName)) match
      case Some((product, quantity)) =>
        val sets = quantity / (buyQuantity + freeQuantity)
        val freeItems = sets * freeQuantity
        
        if freeItems > 0 then
          val discount = product.priceInPence * freeItems
          val desc = description.getOrElse(s"Buy $buyQuantity $productName get $freeQuantity free")
          Some(DiscountResult(desc, discount))
        else None
      case _ => None

// Configurable minimum spend discount
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