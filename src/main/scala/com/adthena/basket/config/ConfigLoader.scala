package com.adthena.basket.config

import com.adthena.basket.domain.*
import scala.io.Source
import scala.util.{Try, Using}
import play.api.libs.json.*

case class ProductConfig(name: String, priceInPence: Int)
case class OfferConfig(`type`: String, config: JsValue)
case class AppConfig(products: List[ProductConfig], offers: List[OfferConfig])

object ConfigLoader:
  
  implicit val productConfigReads: Reads[ProductConfig] = Json.reads[ProductConfig]
  implicit val offerConfigReads: Reads[OfferConfig] = Json.reads[OfferConfig]
  
  def loadProducts(filename: String = "products.json"): Try[List[Product]] =
    loadJsonResource(filename).map { json =>
      (json \ "products").as[List[ProductConfig]].map(pc => 
        Product(pc.name, pc.priceInPence)
      )
    }
  
  def loadOffers(filename: String = "offers.json"): Try[List[OfferConfig]] =
    loadJsonResource(filename).map { json =>
      (json \ "offers").as[List[OfferConfig]]
    }
  
  private def loadJsonResource(filename: String): Try[JsValue] =
    Try {
      Using.resource(getClass.getClassLoader.getResourceAsStream(filename)) { stream =>
        Json.parse(Source.fromInputStream(stream).mkString)
      }
    }