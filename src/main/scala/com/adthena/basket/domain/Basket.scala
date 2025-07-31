package com.adthena.basket.domain

case class Basket(items: Map[Product, Int]):
  def addItem(product: Product, quantity: Int = 1): Basket =
    val currentQuantity = items.getOrElse(product, 0)
    copy(items = items + (product -> (currentQuantity + quantity)))
  
  def getQuantity(product: Product): Int = items.getOrElse(product, 0)
  
  def subtotalInPence: Int = 
    items.map { case (product, quantity) => 
      product.priceInPence * quantity 
    }.sum

object Basket:
  def empty: Basket = Basket(Map.empty)
  
  def fromProducts(products: List[Product]): Basket =
    products.foldLeft(empty)((basket, product) => basket.addItem(product))