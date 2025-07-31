package com.adthena.basket.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class OfferSpec extends AnyFlatSpec with Matchers:

  val soup = Product("Soup", 65)
  val bread = Product("Bread", 80)
  val milk = Product("Milk", 130)
  val apples = Product("Apples", 100)

  "PercentageDiscount" should "apply discount correctly" in {
    val offer = PercentageDiscount("Apples", 10, Some("Apples 10% off"))
    val basket = Basket.empty.addItem(apples, 2)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Apples 10% off"
    result.get.discountInPence shouldBe 20 // 10% of 200p
  }

  it should "not apply when product not in basket" in {
    val offer = PercentageDiscount("Apples", 10)
    val basket = Basket.empty.addItem(milk)
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  it should "be case insensitive" in {
    val offer = PercentageDiscount("apples", 10)
    val basket = Basket.empty.addItem(apples)
    
    val result = offer.apply(basket)
    result shouldBe defined
  }

  it should "use default description when none provided" in {
    val offer = PercentageDiscount("Apples", 15)
    val basket = Basket.empty.addItem(apples)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Apples 15% off"
  }

  it should "handle zero quantity gracefully" in {
    val offer = PercentageDiscount("Apples", 10)
    val basket = Basket.empty
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  "BuyXGetYDiscount" should "apply when conditions met" in {
    val offer = BuyXGetYDiscount("Soup", 2, "Bread", 50, Some("Buy 2 Soup get Bread half price"))
    val basket = Basket.empty
      .addItem(soup, 2)
      .addItem(bread, 1)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Buy 2 Soup get Bread half price"
    result.get.discountInPence shouldBe 40 // 50% of 80p
  }

  it should "not apply without enough trigger items" in {
    val offer = BuyXGetYDiscount("Soup", 2, "Bread", 50)
    val basket = Basket.empty
      .addItem(soup, 1)
      .addItem(bread, 1)
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  it should "not apply without discount items" in {
    val offer = BuyXGetYDiscount("Soup", 2, "Bread", 50)
    val basket = Basket.empty.addItem(soup, 2)
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  it should "limit discounts by available discount items" in {
    val offer = BuyXGetYDiscount("Soup", 1, "Bread", 50) // 1 soup gets bread half price
    val basket = Basket.empty
      .addItem(soup, 3)
      .addItem(bread, 2)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.discountInPence shouldBe 80 // 50% of 2*80p (limited by bread quantity)
  }

  it should "handle multiple trigger sets correctly" in {
    val offer = BuyXGetYDiscount("Soup", 2, "Bread", 50)
    val basket = Basket.empty
      .addItem(soup, 4) // 2 trigger sets
      .addItem(bread, 3) // Only 2 breads get discount
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.discountInPence shouldBe 80 // 50% of 2*80p
  }

  it should "be case insensitive for product names" in {
    val offer = BuyXGetYDiscount("SOUP", 2, "bread", 50)
    val basket = Basket.empty
      .addItem(soup, 2)
      .addItem(bread, 1)
    
    val result = offer.apply(basket)
    result shouldBe defined
  }

  it should "use default description when none provided" in {
    val offer = BuyXGetYDiscount("Soup", 2, "Bread", 25)
    val basket = Basket.empty
      .addItem(soup, 2)
      .addItem(bread, 1)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Buy 2 Soup get Bread 25% off"
  }

  "QuantityDiscount" should "apply buy 2 get 1 free correctly" in {
    val offer = QuantityDiscount("Apples", 2, 1, Some("Buy 2 Apples get 1 free"))
    val basket = Basket.empty.addItem(apples, 3) // Exactly 1 set of 2+1
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Buy 2 Apples get 1 free"
    result.get.discountInPence shouldBe 100 // 1 free apple
  }

  it should "handle multiple sets correctly" in {
    val offer = QuantityDiscount("Apples", 2, 1)
    val basket = Basket.empty.addItem(apples, 6) // 2 sets of 2+1
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.discountInPence shouldBe 200 // 2 free apples
  }

  it should "not apply with insufficient quantity" in {
    val offer = QuantityDiscount("Apples", 2, 1)
    val basket = Basket.empty.addItem(apples, 2) // Not enough for free item
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  it should "handle partial sets correctly" in {
    val offer = QuantityDiscount("Apples", 3, 1) // Buy 3 get 1 free
    val basket = Basket.empty.addItem(apples, 7) // 1 complete set (4), 3 remaining
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.discountInPence shouldBe 100 // Only 1 free apple
  }

  it should "be case insensitive" in {
    val offer = QuantityDiscount("APPLES", 2, 1)
    val basket = Basket.empty.addItem(apples, 3)
    
    val result = offer.apply(basket)
    result shouldBe defined
  }

  it should "use default description when none provided" in {
    val offer = QuantityDiscount("Apples", 3, 2)
    val basket = Basket.empty.addItem(apples, 5)
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "Buy 3 Apples get 2 free"
  }

  "MinimumSpendDiscount" should "apply when minimum met" in {
    val offer = MinimumSpendDiscount(300, 10, Some("10% off orders over £3"))
    val basket = Basket.empty
      .addItem(apples, 2)
      .addItem(milk, 1) // Total: 330p
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "10% off orders over £3"
    result.get.discountInPence shouldBe 33 // 10% of 330p
  }

  it should "not apply when minimum not met" in {
    val offer = MinimumSpendDiscount(300, 10)
    val basket = Basket.empty.addItem(milk, 1) // Total: 130p
    
    val result = offer.apply(basket)
    result shouldBe empty
  }

  it should "apply when minimum exactly met" in {
    val offer = MinimumSpendDiscount(200, 5)
    val basket = Basket.empty.addItem(apples, 2) // Exactly 200p
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.discountInPence shouldBe 10 // 5% of 200p
  }

  it should "use default description when none provided" in {
    val offer = MinimumSpendDiscount(150, 15)
    val basket = Basket.empty.addItem(apples, 2) // 200p
    
    val result = offer.apply(basket)
    result shouldBe defined
    result.get.description shouldBe "15% off orders over £150"
  }

  it should "handle empty basket" in {
    val offer = MinimumSpendDiscount(100, 10)
    val basket = Basket.empty
    
    val result = offer.apply(basket)
    result shouldBe empty
  }
