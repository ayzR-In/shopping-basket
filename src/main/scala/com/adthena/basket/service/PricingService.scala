package com.adthena.basket.service

import com.adthena.basket.domain.*
import com.adthena.basket.utils.MoneyUtils
import scala.util.{Try, Success, Failure}

case class PricingResult(
  subtotalInPence: Int,
  discounts: List[DiscountResult], 
  totalInPence: Int
):
  def formatOutput: String =
    val subtotalFormatted = MoneyUtils.formatPence(subtotalInPence)
    val totalDiscount = discounts.map(_.discountInPence).sum
    val totalFormatted = MoneyUtils.formatPence(subtotalInPence - totalDiscount)
    
    val discountLines = if discounts.isEmpty then
      List("(No offers available)")
    else
      discounts.map(d => s"${d.description}: ${MoneyUtils.formatPence(d.discountInPence)}")
    
    val lines = List(s"Subtotal: $subtotalFormatted") ++ 
                discountLines ++ 
                List(s"Total price: $totalFormatted")
    
    lines.mkString("\n")

// Primary constructor for testing with direct dependencies
class PricingService(
  private val productService: ProductService,
  private val offerService: OfferService
):
  
  def calculatePrice(productNames: List[String]): Either[String, PricingResult] =
    val productResults = productNames.map { name =>
      productService.findByName(name) match
        case Some(product) => Right(product)
        case None => Left(name)
    }
    
    val invalidNames = productResults.collect { case Left(name) => name }
    val validProducts = productResults.collect { case Right(product) => product }
    
    if invalidNames.nonEmpty then
      Left(s"Invalid products: ${invalidNames.mkString(", ")}")
    else
      val basket = Basket.fromProducts(validProducts)
      val subtotal = basket.subtotalInPence
      val discounts = offerService.applyOffers(basket)
      val totalDiscount = discounts.map(_.discountInPence).sum
      
      Right(PricingResult(
        subtotalInPence = subtotal,
        discounts = discounts,
        totalInPence = subtotal - totalDiscount
      ))

object PricingService:
  // Factory method for production use with configuration loading
  def fromConfig(
    productConfig: String = "products.json",
    offerConfig: String = "offers.json"
  ): Try[PricingService] =
    for
      productService <- ProductService.fromConfig(productConfig)
      offerService <- OfferService.fromConfig(offerConfig)
    yield new PricingService(productService, offerService)
  
  // Convenience factory method for testing
  def apply(productService: ProductService, offerService: OfferService): PricingService = 
    new PricingService(productService, offerService)