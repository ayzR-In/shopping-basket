package com.adthena.basket.service

import com.adthena.basket.domain.{Offer, Basket, DiscountResult}
import com.adthena.basket.config.{ConfigLoader, OfferFactory}
import scala.util.{Try, Success, Failure}

// Primary constructor for testing with direct dependencies
class OfferService(private val offers: List[Offer]):
  
  def applyOffers(basket: Basket): List[DiscountResult] =
    offers.flatMap(_.apply(basket))

object OfferService:
  // Factory method for production use with configuration loading
  def fromConfig(configFile: String = "offers.json"): Try[OfferService] =
    for
      offerConfigs <- ConfigLoader.loadOffers(configFile)
      offers <- Try(offerConfigs.map(OfferFactory.createOffer).collect { case Success(offer) => offer })
    yield new OfferService(offers)
  
  // Convenience factory method for testing
  def apply(offers: List[Offer]): OfferService = new OfferService(offers)