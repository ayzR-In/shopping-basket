error id: file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Basket.scala:`<none>`.
file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Basket.scala
empty definition using pc, found symbol in pc: `<none>`.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 403
uri: file://<WORKSPACE>/src/main/scala/com/adthena/basket/domain/Basket.scala
text:
```scala
package com.adthena.basket.domain

case class Basket(items: Map[Product, Int]):
    def addItem(product: Product, quantity: Int = 1): Basket = 
        val currentQuantity = items.getOrElse(product, 0)
        copy(items = items + (product -> (currentQuantity + quantity)))

    def getQuantity(product: Product): Int = items.getOrElse(product, 0)

    def subtotalInPence: Int = 
        items.map { ca@@se (product, quantity) => 
            product.priceInPence * quantity
        }.sum

object Basket:
    def empty: Basket = Basket(Map.empty)

    def fromProducts(products: List[Product]): Basket =
        products.foldLeft(empty)((basket, product) => basket.addItem(product))
```


#### Short summary: 

empty definition using pc, found symbol in pc: `<none>`.