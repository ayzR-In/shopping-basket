package com.adthena.basket.domain

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BasketSpec extends AnyFlatSpec with Matchers:

  val soup = Product("Soup", 65)
  val bread = Product("Bread", 80)
  val milk = Product("Milk", 130)
  val apples = Product("Apples", 100)

  "Basket" should "start empty" in {
    val basket = Basket.empty
    basket.items shouldBe empty
    basket.subtotalInPence shouldBe 0
  }

  it should "add single item correctly" in {
    val basket = Basket.empty.addItem(soup)
    basket.getQuantity(soup) shouldBe 1
    basket.subtotalInPence shouldBe 65
  }

  it should "add multiple quantities of same item" in {
    val basket = Basket.empty.addItem(soup, 3)
    basket.getQuantity(soup) shouldBe 3
    basket.subtotalInPence shouldBe 195
  }

  it should "accumulate items when added multiple times" in {
    val basket = Basket.empty
      .addItem(soup, 2)
      .addItem(soup, 1)
    
    basket.getQuantity(soup) shouldBe 3
    basket.subtotalInPence shouldBe 195
  }

  it should "handle multiple different products" in {
    val basket = Basket.empty
      .addItem(soup, 2)
      .addItem(bread, 1)
      .addItem(milk, 1)
    
    basket.getQuantity(soup) shouldBe 2
    basket.getQuantity(bread) shouldBe 1
    basket.getQuantity(milk) shouldBe 1
    basket.getQuantity(apples) shouldBe 0
    
    basket.subtotalInPence shouldBe 340 // 2*65 + 80 + 130
  }

  it should "create basket from product list" in {
    val products = List(soup, soup, bread, milk)
    val basket = Basket.fromProducts(products)
    
    basket.getQuantity(soup) shouldBe 2
    basket.getQuantity(bread) shouldBe 1
    basket.getQuantity(milk) shouldBe 1
    basket.subtotalInPence shouldBe 340
  }

  it should "handle duplicate products in fromProducts" in {
    val products = List(apples, apples, apples)
    val basket = Basket.fromProducts(products)
    
    basket.getQuantity(apples) shouldBe 3
    basket.subtotalInPence shouldBe 300
  }

  it should "be immutable when adding items" in {
    val originalBasket = Basket.empty.addItem(soup)
    val newBasket = originalBasket.addItem(bread)
    
    originalBasket.getQuantity(soup) shouldBe 1
    originalBasket.getQuantity(bread) shouldBe 0
    
    newBasket.getQuantity(soup) shouldBe 1
    newBasket.getQuantity(bread) shouldBe 1
  }

  it should "return zero quantity for non-existent products" in {
    val basket = Basket.empty.addItem(soup)
    basket.getQuantity(bread) shouldBe 0
  }

  it should "handle empty fromProducts list" in {
    val basket = Basket.fromProducts(List.empty)
    basket.items shouldBe empty
    basket.subtotalInPence shouldBe 0
  }