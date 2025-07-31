file://<WORKSPACE>/src/main/scala/com/adthena/basket/config/ConfigLoader.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 984
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

    def loadProducts(filename: String = "product.json"): Try[List[Product]] =
        loadJsonResouce(filename).map { json =>
            (json \ "products").as[List[ProductConfig]].map(pc =>
                Product(pc.name, pc.priceInPence)    
            )
        }
    
    def loadOffers(filename: String = "products.json"): Try[List[OfferConfig]] =
        loadJsonResouce(filename).map { json =>
            (json \ "offers").as[List[Offer@@Config]]    
        }
    
    private def loadJsonResouce(filename: String): Try[JsValue] =
        Try {
            Using.resource(getClass.getClassLoader.getResourceAsStream(filename)) { stream =>
                Json.parse(Source.fromInputStream(stream).mkString)
            }
        }
```


#### Short summary: 

empty definition using pc, found symbol in pc: 