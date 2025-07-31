package com.adthena.basket.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.adthena.basket.domain.*

class OfferServiceSpec extends AnyFlatSpec with Matchers:

  val soup = Product("Soup", 65)
  val bread = Product("Bread", 80)
  val milk = Product("Milk", 130)
  val apples = Product("Apples", 100)

  val applesDiscount = PercentageDiscount("Apples", 10, Some("Apples 10% off"))
  val soupBreadOffer = BuyXGetYDiscount("Soup", 2, "Bread", 50, Some("Buy 2 Soup get Bread half price"))
  val quantityOffer = QuantityDiscount("Milk", 2, 1, Some("Buy 2 Milk get 1 free"))
  val minimumSpendOffer = MinimumSpendDiscount(500, 5, Some("5% off orders over £5"))

  "OfferService" should "apply single applicable offer" in {
    val offers = List(applesDiscount)
    val offerService = OfferService(offers)
    val basket = Basket.empty.addItem(apples, 1)
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 1
    discounts.head.description shouldBe "Apples 10% off"
    discounts.head.discountInPence shouldBe 10
  }

  it should "apply multiple applicable offers" in {
    val offers = List(applesDiscount, soupBreadOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
      .addItem(apples, 1)
      .addItem(soup, 2)
      .addItem(bread, 1)
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 2
    
    val descriptions = discounts.map(_.description)
    descriptions should contain("Apples 10% off")
    descriptions should contain("Buy 2 Soup get Bread half price")
    
    val totalDiscount = discounts.map(_.discountInPence).sum
    totalDiscount shouldBe 50 // 10p + 40p
  }

  it should "return empty list when no offers applicable" in {
    val offers = List(applesDiscount, soupBreadOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty.addItem(milk, 1) // No offers for milk alone
    
    val discounts = offerService.applyOffers(basket)
    discounts shouldBe empty
  }

  it should "handle empty offers list" in {
    val offerService = OfferService(List.empty)
    val basket = Basket.empty.addItem(apples, 1)
    
    val discounts = offerService.applyOffers(basket)
    discounts shouldBe empty
  }

  it should "handle empty basket" in {
    val offers = List(applesDiscount, soupBreadOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
    
    val discounts = offerService.applyOffers(basket)
    discounts shouldBe empty
  }

  it should "apply offers in order defined" in {
    val offers = List(applesDiscount, soupBreadOffer, quantityOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
      .addItem(apples, 1)
      .addItem(soup, 2)
      .addItem(bread, 1)
      .addItem(milk, 3)
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 3
    
    // Should maintain the order of offers as defined
    discounts(0).description shouldBe "Apples 10% off"
    discounts(1).description shouldBe "Buy 2 Soup get Bread half price"
    discounts(2).description shouldBe "Buy 2 Milk get 1 free"
  }

  it should "handle complex basket with all offer types" in {
    val offers = List(applesDiscount, soupBreadOffer, quantityOffer, minimumSpendOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
      .addItem(apples, 2)    // 200p, triggers 10% discount
      .addItem(soup, 2)      // 130p, triggers bread offer
      .addItem(bread, 1)     // 80p, gets 50% discount
      .addItem(milk, 3)      // 390p, triggers buy 2 get 1 free
    // Total: 800p, triggers 5% minimum spend discount
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 4
    
    val totalBasket = basket.subtotalInPence
    totalBasket shouldBe 800
    
    val descriptions = discounts.map(_.description)
    descriptions should contain("Apples 10% off")
    descriptions should contain("Buy 2 Soup get Bread half price")
    descriptions should contain("Buy 2 Milk get 1 free")
    descriptions should contain("5% off orders over £5")
  }

  it should "handle offers that don't apply due to insufficient quantities" in {
    val offers = List(soupBreadOffer, quantityOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
      .addItem(soup, 1)    // Not enough for soup offer (needs 2)
      .addItem(bread, 1)
      .addItem(milk, 2)    // Not enough for quantity offer (needs 3 for buy 2 get 1)
    
    val discounts = offerService.applyOffers(basket)
    discounts shouldBe empty
  }

  it should "apply only offers that meet conditions" in {
    val offers = List(applesDiscount, soupBreadOffer, quantityOffer)
    val offerService = OfferService(offers)
    val basket = Basket.empty
      .addItem(apples, 1)  // Triggers apple discount
      .addItem(soup, 1)    // Not enough for soup offer
      .addItem(milk, 2)    // Not enough for quantity offer
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 1
    discounts.head.description shouldBe "Apples 10% off"
  }

  it should "handle duplicate offers correctly" in {
    val duplicateOffers = List(applesDiscount, applesDiscount)
    val offerService = OfferService(duplicateOffers)
    val basket = Basket.empty.addItem(apples, 1)
    
    val discounts = offerService.applyOffers(basket)
    discounts should have size 2 // Both offers apply
    discounts.foreach(_.discountInPence shouldBe 10)
  }