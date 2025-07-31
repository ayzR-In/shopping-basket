# Shopping Basket - Scala 3 Technical Assessment

A configurable shopping basket pricing system that calculates subtotals, applies discount offers, and displays final prices. Built with Scala 3 using a domain-driven design approach with full extensibility for products and offers through JSON configuration.

## Features

- ✅ **Command-line interface** for basket pricing
- ✅ **Configurable products** via JSON (no code changes required)
- ✅ **Extensible offer system** with multiple discount types
- ✅ **Proper money handling** (avoids floating-point precision issues)
- ✅ **Comprehensive unit tests** with edge case coverage
- ✅ **Clean architecture** with domain-driven design

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 11+** (Java 17+ recommended)
- **SBT 1.9.6+** ([Install SBT](https://www.scala-sbt.org/download.html))
- **Scala 3.3.6** (managed by SBT)

### Verify Prerequisites

```bash
# Check Java version
java -version

# Check SBT version  
sbt version
```

## Quick Start

### 1. Clone and Setup

```bash
git clone <repository-url>
cd shopping-basket
```

### 2. Compile the Project

```bash
sbt clean compile
```

### 3. Run the Application

```bash
# Basic usage
sbt "run Apples Milk Bread"

# Multiple items of same product
sbt "run Soup Soup Bread Apples"

# Single item
sbt "run Milk"
```

### 4. Run Unit Tests

```bash
# Run all tests
sbt test

# Run tests with detailed output
sbt "testOnly * -- -oD"

# Run specific test class
sbt "testOnly *PricingServiceSpec"
```

## Usage Examples

### Example 1: Basic Usage
```bash
$ sbt "run Apples Milk Bread"
Subtotal: £3.10
Apples 10% off: 10p
Total price: £3.00
```

### Example 2: Soup and Bread Offer
```bash
$ sbt "run Soup Soup Bread"
Subtotal: £2.10
Buy 2 Soup get Bread half price: 40p
Total price: £1.70
```

### Example 3: No Offers Available
```bash
$ sbt "run Milk"
Subtotal: £1.30
(No offers available)
Total price: £1.30
```

### Example 4: Multiple Offers
```bash
$ sbt "run Apples Soup Soup Bread"
Subtotal: £3.10
Apples 10% off: 10p
Buy 2 Soup get Bread half price: 40p
Total price: £2.60
```

### Example 5: Invalid Product
```bash
$ sbt "run Pizza"
Invalid products: Pizza
```

## Available Products

| Product | Price   |
|---------|---------|
| Soup    | 65p     |
| Bread   | 80p     |
| Milk    | £1.30   |
| Apples  | £1.00   |

## Current Offers

| Offer | Description |
|-------|-------------|
| Apples Discount | 10% off Apples |
| Soup + Bread Combo | Buy 2 tins of Soup, get Bread for half price |

## Project Structure

```
shopping-basket/
├── build.sbt                          # SBT build configuration
├── project/build.properties           # SBT version
├── src/main/
│   ├── scala/com/adthena/basket/
│   │   ├── Main.scala                  # Application entry point
│   │   ├── domain/                     # Core domain models
│   │   │   ├── Product.scala           # Product case class
│   │   │   ├── Basket.scala            # Basket with items
│   │   │   └── Offer.scala             # Offer system
│   │   ├── service/                    # Business logic layer
│   │   │   ├── PricingService.scala    # Main pricing logic
│   │   │   ├── ProductService.scala    # Product management
│   │   │   └── OfferService.scala      # Offer management  
│   │   ├── config/                     # Configuration loading
│   │   │   ├── ConfigLoader.scala      # JSON config loader
│   │   │   └── OfferFactory.scala      # Dynamic offer creation
│   │   └── utils/
│   │       └── MoneyUtils.scala        # Currency formatting
│   └── resources/
│       ├── products.json               # Product configuration
│       └── offers.json                 # Offers configuration
└── src/test/scala/                     # Unit tests
```

## Configuration

### Adding New Products

Edit `src/main/resources/products.json`:

```json
{
  "products": [
    {
      "name": "Soup",
      "priceInPence": 65
    },
    {
      "name": "NewProduct", 
      "priceInPence": 150
    }
  ]
}
```

### Adding New Offers

Edit `src/main/resources/offers.json`:

```json
{
  "offers": [
    {
      "type": "PercentageDiscount",
      "config": {
        "productName": "NewProduct",
        "percentage": 15,
        "description": "NewProduct 15% off"
      }
    }
  ]
}
```

### Available Offer Types

1. **PercentageDiscount**: X% off a specific product
2. **BuyXGetYDiscount**: Buy X of product A, get Y% off product B  
3. **QuantityDiscount**: Buy X get Y free
4. **MinimumSpendDiscount**: X% off orders over £Y

## Development

### Running in Development Mode

```bash
# Auto-compile on file changes
sbt ~compile

# Auto-run tests on file changes  
sbt ~test

# Interactive mode
sbt
> run Apples Milk
> test
> exit
```

### Creating a Runnable JAR

```bash
# Add sbt-assembly plugin to project/plugins.sbt first:
echo 'addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.3")' >> project/plugins.sbt

# Create fat JAR
sbt assembly

# Run the JAR
java -jar target/scala-3.3.6/shopping-basket-assembly-*.jar Apples Milk Bread
```

### IDE Setup

#### IntelliJ IDEA
1. Install Scala plugin
2. Import project via SBT
3. Set Project SDK to Java 11+
4. Configure Scala SDK to 3.3.6

#### VS Code  
1. Install "Metals" extension
2. Open project folder
3. Metals will auto-configure the project

## Testing

### Running Tests

```bash
# All tests
sbt test

# Specific test suite
sbt "testOnly *PricingServiceSpec"

# Tests with coverage (add scoverage plugin)
sbt coverage test coverageReport
```

### Test Coverage

The test suite covers:
- ✅ Individual product pricing
- ✅ Percentage discount application  
- ✅ Buy X Get Y discount logic
- ✅ Multiple offer combinations
- ✅ Edge cases (empty basket, invalid products)
- ✅ Money formatting (pence vs pounds)
- ✅ Configuration loading errors

## Troubleshooting

### Common Issues

#### Compilation Errors
```bash
# Clean and rebuild
sbt clean compile
```

#### Missing Configuration Files
Ensure these files exist:
- `src/main/resources/products.json`
- `src/main/resources/offers.json`

#### Java Version Issues
```bash
# Check Java version (needs 11+)
java -version

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java11
```

#### SBT Memory Issues
```bash
# Increase JVM memory
export SBT_OPTS="-Xmx2G -XX:+UseConcMarkSweepGC"
```

### Debug Mode

```bash
# Run with debug logging
sbt -Dlogback.configurationFile=logback-debug.xml run Apples Milk
```

## Architecture Highlights

### Extensibility
- **No code changes** required for new products or offers
- **Configuration-driven** business logic  
- **Plugin architecture** for new offer types

### Design Patterns
- **Domain-Driven Design** with clear domain models
- **Factory Pattern** for dynamic offer creation
- **Service Layer** for business logic separation  
- **Configuration Abstraction** for external setup

### Scala 3 Features Used
- **Enums** for type-safe modeling
- **Case Classes** for immutable data
- **Pattern Matching** for control flow
- **Extension Methods** for enhanced APIs
- **@main annotation** for application entry

## Performance

- **Memory efficient**: Immutable data structures
- **Fast startup**: Minimal dependencies  
- **Scalable**: Functional programming approach
- **Thread-safe**: No mutable shared state

## License

This project is created for technical assessment purposes.

## Support

For questions about this implementation, please contact the development team or raise an issue in the repository.