package com.adthena.basket

import com.adthena.basket.service.PricingService
import scala.util.{Success, Failure}

@main def priceBasket(args: String*): Unit =
  if args.isEmpty then
    println("Usage: PriceBasket item1 item2 item3 ...")
    sys.exit(1)
  
  PricingService.fromConfig() match
    case Success(pricingService) =>
      pricingService.calculatePrice(args.toList) match
        case Right(result) => 
          println(result.formatOutput)
        case Left(error) =>
          println(error)
          sys.exit(1)
    
    case Failure(exception) =>
      println(s"Failed to load configuration: ${exception.getMessage}")
      sys.exit(1)