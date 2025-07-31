package com.adthena.basket.utils

object MoneyUtils:
  def formatPence(pence: Int): String =
    val pounds = pence / 100
    val remainingPence = pence % 100
    
    if pounds > 0 then
      if remainingPence > 0 then f"£${pounds}.${remainingPence}%02d"
      else s"£${pounds}.00"
    else
      s"${pence}p"