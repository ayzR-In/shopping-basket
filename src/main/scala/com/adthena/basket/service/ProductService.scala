package com.adthena.basket.service

import com.adthena.basket.domain.Product
import com.adthena.basket.config.ConfigLoader
import scala.util.{Try, Success, Failure}

// Primary constructor for testing with direct dependencies
class ProductService(private val products: List[Product]):
  
  def findByName(name: String): Option[Product] =
    if name.trim.isEmpty then None
    else products.find(_.name.equalsIgnoreCase(name.trim))
  
  def getAllProducts: List[Product] = products

object ProductService:
  // Factory method for production use with configuration loading
  def fromConfig(configFile: String = "products.json"): Try[ProductService] =
    ConfigLoader.loadProducts(configFile).map(new ProductService(_))
  
  // Convenience factory method for testing
  def apply(products: List[Product]): ProductService = new ProductService(products)