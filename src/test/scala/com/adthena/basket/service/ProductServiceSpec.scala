package com.adthena.basket.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.adthena.basket.domain.Product

class ProductServiceSpec extends AnyFlatSpec with Matchers:

  val soup = Product("Soup", 65)
  val bread = Product("Bread", 80)
  val milk = Product("Milk", 130)
  val apples = Product("Apples", 100)
  val products = List(soup, bread, milk, apples)
  
  val productService = ProductService(products)

  "ProductService" should "find products by exact name" in {
    productService.findByName("Soup") shouldBe Some(soup)
    productService.findByName("Bread") shouldBe Some(bread)
    productService.findByName("Milk") shouldBe Some(milk)
    productService.findByName("Apples") shouldBe Some(apples)
  }

  it should "be case insensitive" in {
    productService.findByName("soup") shouldBe Some(soup)
    productService.findByName("BREAD") shouldBe Some(bread)
    productService.findByName("MiLk") shouldBe Some(milk)
    productService.findByName("aPpLeS") shouldBe Some(apples)
  }

  it should "return None for unknown products" in {
    productService.findByName("Pizza") shouldBe None
    productService.findByName("Hamburger") shouldBe None
    productService.findByName("Cheese") shouldBe None
    productService.findByName("Tomatoes") shouldBe None
  }

  it should "return None for empty string" in {
    productService.findByName("") shouldBe None
  }

  it should "return None for whitespace-only strings" in {
    productService.findByName("   ") shouldBe None
    productService.findByName("\t") shouldBe None
    productService.findByName("\n") shouldBe None
  }

  it should "handle products with whitespace in names" in {
    val productWithSpaces = Product("Apple Juice", 150)
    val serviceWithSpaces = ProductService(List(productWithSpaces))
    
    serviceWithSpaces.findByName("Apple Juice") shouldBe Some(productWithSpaces)
    serviceWithSpaces.findByName("apple juice") shouldBe Some(productWithSpaces)
    serviceWithSpaces.findByName("APPLE JUICE") shouldBe Some(productWithSpaces)
  }

  it should "return all products correctly" in {
    val allProducts = productService.getAllProducts
    allProducts should contain theSameElementsAs products
    allProducts should have size 4
  }

  it should "handle empty product list" in {
    val emptyService = ProductService(List.empty)
    
    emptyService.findByName("Soup") shouldBe None
    emptyService.getAllProducts shouldBe empty
  }

  it should "handle duplicate product names correctly" in {
    val duplicateProducts = List(
      Product("Soup", 65),
      Product("Soup", 70) // Different price
    )
    val serviceWithDuplicates = ProductService(duplicateProducts)
    
    // Should return the first match
    val result = serviceWithDuplicates.findByName("Soup")
    result shouldBe defined
    result.get.priceInPence shouldBe 65
  }

  it should "maintain product ordering" in {
    val orderedProducts = List(soup, bread, milk, apples)
    val orderedService = ProductService(orderedProducts)
    
    orderedService.getAllProducts should equal(orderedProducts)
  }

  it should "handle products with special characters" in {
    val specialProducts = List(
      Product("Coq-au-Vin", 500),
      Product("Café Latte", 350),
      Product("Piña Colada", 400)
    )
    val specialService = ProductService(specialProducts)
    
    specialService.findByName("Coq-au-Vin") shouldBe Some(specialProducts(0))
    specialService.findByName("coq-au-vin") shouldBe Some(specialProducts(0))
    specialService.findByName("CAFÉ LATTE") shouldBe Some(specialProducts(1))
  }
