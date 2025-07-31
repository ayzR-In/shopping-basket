package com.adthena.basket.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.BeforeAndAfterEach
import com.adthena.basket.domain.*

class PricingServiceSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach:

  // Test products
  val soup = Product("Soup", 65)
  val bread = Product("Bread", 80)
  val milk = Product("Milk", 130)
  val apples = Product("Apples", 100)
  val testProducts = List(soup, bread, milk, apples)

  // Test offers
  val applesDiscount = PercentageDiscount("Apples", 10, Some("Apples 10% off"))
  val soupBreadOffer = BuyXGetYDiscount("Soup", 2, "Bread", 50, Some("Buy 2 Soup get Bread half price"))
  val testOffers = List(applesDiscount, soupBreadOffer)

  val productService = ProductService(testProducts)
  val offerService = OfferService(testOffers)
  val pricingService = PricingService(productService, offerService)

  "PricingService" should "calculate correct price for single milk item" in {
    val result = pricingService.calculatePrice(List("Milk"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 130
    pricingResult.discounts shouldBe empty
    pricingResult.totalInPence shouldBe 130
  }

  it should "handle the assessment example: Apples Milk Bread" in {
    val result = pricingService.calculatePrice(List("Apples", "Milk", "Bread"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 310 // 100 + 130 + 80
    pricingResult.discounts should have size 1
    pricingResult.discounts.head.description shouldBe "Apples 10% off"
    pricingResult.discounts.head.discountInPence shouldBe 10
    pricingResult.totalInPence shouldBe 300
  }

  it should "apply 10% discount on apples correctly" in {
    val result = pricingService.calculatePrice(List("Apples"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 100
    pricingResult.discounts should have size 1
    pricingResult.discounts.head.discountInPence shouldBe 10 // 10% of 100p
    pricingResult.totalInPence shouldBe 90
  }

  it should "apply soup and bread offer correctly" in {
    val result = pricingService.calculatePrice(List("Soup", "Soup", "Bread"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 210 // 65 + 65 + 80
    pricingResult.discounts should have size 1
    pricingResult.discounts.head.discountInPence shouldBe 40 // 50% of 80p
    pricingResult.totalInPence shouldBe 170
  }

  it should "apply multiple offers when applicable" in {
    val result = pricingService.calculatePrice(List("Apples", "Soup", "Soup", "Bread"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 310 // 100 + 65 + 65 + 80
    pricingResult.discounts should have size 2
    
    val discountDescriptions = pricingResult.discounts.map(_.description)
    discountDescriptions should contain("Apples 10% off")
    discountDescriptions should contain("Buy 2 Soup get Bread half price")
    
    val totalDiscount = pricingResult.discounts.map(_.discountInPence).sum
    totalDiscount shouldBe 50 // 10p (apples) + 40p (bread)
    pricingResult.totalInPence shouldBe 260
  }

  it should "handle multiple items of same product" in {
    val result = pricingService.calculatePrice(List("Apples", "Apples", "Apples"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 300 // 3 * 100p
    pricingResult.discounts should have size 1
    pricingResult.discounts.head.discountInPence shouldBe 30 // 10% of 300p
    pricingResult.totalInPence shouldBe 270
  }

  it should "not apply soup offer without enough soup" in {
    val result = pricingService.calculatePrice(List("Soup", "Bread"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 145 // 65 + 80
    pricingResult.discounts shouldBe empty
    pricingResult.totalInPence shouldBe 145
  }

  it should "not apply soup offer without bread" in {
    val result = pricingService.calculatePrice(List("Soup", "Soup"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 130 // 65 + 65
    pricingResult.discounts shouldBe empty
    pricingResult.totalInPence shouldBe 130
  }

  it should "handle case insensitive product names" in {
    val result = pricingService.calculatePrice(List("apples", "MILK", "BrEaD"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 310
    pricingResult.discounts should have size 1
    pricingResult.totalInPence shouldBe 300
  }

  it should "return error for invalid products" in {
    val result = pricingService.calculatePrice(List("Pizza", "Hamburger"))
    
    result shouldBe a[Left[_, _]]
    val error = result.swap.getOrElse(fail("Should return Left"))
    error should include("Invalid products: Pizza, Hamburger")
  }

  it should "return error for mix of valid and invalid products" in {
    val result = pricingService.calculatePrice(List("Milk", "Pizza", "Bread"))
    
    result shouldBe a[Left[_, _]]
    val error = result.swap.getOrElse(fail("Should return Left"))
    error should include("Invalid products: Pizza")
  }

  it should "handle empty basket" in {
    val result = pricingService.calculatePrice(List.empty)
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 0
    pricingResult.discounts shouldBe empty
    pricingResult.totalInPence shouldBe 0
  }

  it should "format output correctly for assessment example" in {
    val result = pricingService.calculatePrice(List("Apples", "Milk", "Bread"))
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    val output = pricingResult.formatOutput
    output should include("Subtotal: £3.10")
    output should include("Apples 10% off: 10p")
    output should include("Total price: £3.00")
  }

  it should "format output correctly when no offers available" in {
    val result = pricingService.calculatePrice(List("Milk"))
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    val output = pricingResult.formatOutput
    output should include("Subtotal: £1.30")
    output should include("(No offers available)")
    output should include("Total price: £1.30")
  }

  it should "handle maximum soup bread combinations correctly" in {
    // 4 soups, 2 breads -> should get 2 bread discounts (limited by bread quantity)
    val result = pricingService.calculatePrice(List("Soup", "Soup", "Soup", "Soup", "Bread", "Bread"))
    
    result shouldBe a[Right[_, _]]
    val pricingResult = result.getOrElse(fail("Should return Right"))
    
    pricingResult.subtotalInPence shouldBe 420 // 4*65 + 2*80
    pricingResult.discounts should have size 1
    pricingResult.discounts.head.discountInPence shouldBe 80 // 50% of 2*80p
    pricingResult.totalInPence shouldBe 340
  }