package com.adthena.basket.config

import com.adthena.basket.domain.*
import play.api.libs.json.*
import scala.util.{Try, Success, Failure}

object OfferFactory:
  
  def createOffer(offerConfig: OfferConfig): Try[Offer] =
    offerConfig.`type` match
      case "PercentageDiscount" => createPercentageDiscount(offerConfig.config)
      case "BuyXGetYDiscount" => createBuyXGetYDiscount(offerConfig.config)
      case "QuantityDiscount" => createQuantityDiscount(offerConfig.config)
      case "MinimumSpendDiscount" => createMinimumSpendDiscount(offerConfig.config)
      case unknown => Failure(new IllegalArgumentException(s"Unknown offer type: $unknown"))
  
  private def createPercentageDiscount(config: JsValue): Try[Offer] =
    Try {
      PercentageDiscount(
        productName = (config \ "productName").as[String],
        percentage = (config \ "percentage").as[Int],
        description = (config \ "description").asOpt[String]
      )
    }
  
  private def createBuyXGetYDiscount(config: JsValue): Try[Offer] =
    Try {
      BuyXGetYDiscount(
        triggerProductName = (config \ "triggerProductName").as[String],
        triggerQuantity = (config \ "triggerQuantity").as[Int],
        discountProductName = (config \ "discountProductName").as[String],
        discountPercentage = (config \ "discountPercentage").as[Int],
        description = (config \ "description").asOpt[String]
      )
    }
  
  private def createQuantityDiscount(config: JsValue): Try[Offer] =
    Try {
      QuantityDiscount(
        productName = (config \ "productName").as[String],
        buyQuantity = (config \ "buyQuantity").as[Int],
        freeQuantity = (config \ "freeQuantity").as[Int],
        description = (config \ "description").asOpt[String]
      )
    }
  
  private def createMinimumSpendDiscount(config: JsValue): Try[Offer] =
    Try {
      MinimumSpendDiscount(
        minimumSpendInPence = (config \ "minimumSpendInPence").as[Int],
        discountPercentage = (config \ "discountPercentage").as[Int],
        description = (config \ "description").asOpt[String]
      )
    }