package com.adthena.basket.utils

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MoneyUtilsSpec extends AnyFlatSpec with Matchers:

  "MoneyUtils.formatPence" should "format small pence amounts correctly" in {
    MoneyUtils.formatPence(1) shouldBe "1p"
    MoneyUtils.formatPence(10) shouldBe "10p"
    MoneyUtils.formatPence(99) shouldBe "99p"
    MoneyUtils.formatPence(65) shouldBe "65p"
  }

  it should "format pounds with pence correctly" in {
    MoneyUtils.formatPence(101) shouldBe "£1.01"
    MoneyUtils.formatPence(110) shouldBe "£1.10"
    MoneyUtils.formatPence(310) shouldBe "£3.10"
    MoneyUtils.formatPence(1299) shouldBe "£12.99"
    MoneyUtils.formatPence(1509) shouldBe "£15.09"
  }

  it should "format whole pounds correctly" in {
    MoneyUtils.formatPence(100) shouldBe "£1.00"
    MoneyUtils.formatPence(200) shouldBe "£2.00"
    MoneyUtils.formatPence(500) shouldBe "£5.00"
    MoneyUtils.formatPence(1000) shouldBe "£10.00"
    MoneyUtils.formatPence(2500) shouldBe "£25.00"
  }

  it should "handle zero correctly" in {
    MoneyUtils.formatPence(0) shouldBe "0p"
  }

  it should "handle single digit pence with pounds" in {
    MoneyUtils.formatPence(105) shouldBe "£1.05"
    MoneyUtils.formatPence(203) shouldBe "£2.03"
    MoneyUtils.formatPence(1001) shouldBe "£10.01"
  }

  it should "handle large amounts correctly" in {
    MoneyUtils.formatPence(9999) shouldBe "£99.99"
    MoneyUtils.formatPence(10000) shouldBe "£100.00"
    MoneyUtils.formatPence(12345) shouldBe "£123.45"
  }

  it should "handle edge case of 1 pound exactly" in {
    MoneyUtils.formatPence(100) shouldBe "£1.00"
  }

  it should "handle assessment example amounts correctly" in {
    MoneyUtils.formatPence(310) shouldBe "£3.10"  // Subtotal from example
    MoneyUtils.formatPence(10) shouldBe "10p"     // Discount amount
    MoneyUtils.formatPence(300) shouldBe "£3.00"  // Final total
    MoneyUtils.formatPence(130) shouldBe "£1.30"  // Milk price
  }
