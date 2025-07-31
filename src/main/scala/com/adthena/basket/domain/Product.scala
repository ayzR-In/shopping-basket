package com.adthena.basket.domain

case class Product(name: String, priceInPence: Int)

object Product:
  def apply(name: String, priceInPounds: Double): Product =
    Product(name, (priceInPounds * 100).toInt)