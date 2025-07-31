error id: file://<WORKSPACE>/src/main/scala/com/adthena/basket/config/ConfigLoader.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/com/adthena/basket/config/ConfigLoader.scala
empty definition using pc, found symbol in pc: `<none>`.
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 675
uri: file://<WORKSPACE>/src/main/scala/com/adthena/basket/config/ConfigLoader.scala
text:
```scala
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
      (json \ "pr@@oducts").as[List[ProductConfig]].map(pc => 
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
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.