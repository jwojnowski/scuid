# Scuid 🦑 — Cuid2 for Scala
[![License](http://img.shields.io/:license-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/me.wojnowski/scuid_3.svg?color=blue)](https://search.maven.org/search?q=scuid)
[![Known Vulnerabilities](https://snyk.io/test/github/jwojnowski/scuid/badge.svg)](https://snyk.io/test/github/jwojnowski/scuid)

Functional Scala implementation of [Cuid2](https://github.com/paralleldrive/cuid2).

### Cuid2 examples
```
sa9vplf9bpyqj6hui8rsvi27
nfehj5xe9g7aruem87e1dcb6
p50d8uxvzs0irgrl9sidxqos
```

### Benefits over other IDs
- secure
- collision-resistant
- optimized for horizontal scaling and performance
- friendlier to humans (easier to distinguish visually, double-click-selectable)

A full comparison to other IDs can be found in the readme 
of the [original implementation](https://github.com/paralleldrive/cuid2?tab=readme-ov-file#the-contenders).

## Getting started

### SBT
```scala
libraryDependencies += "me.wojnowski" %% "scuid" % "<version>"
```

### Usage (Cats Effect)
```scala
import cats.effect.IO
import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Gen

...

for {
  cuid2Generator: Cuid2Gen[IO] <- Cuid2Gen.default[IO]
  id: Cuid2                    <- cuid2Generator.generate // q055n575se3xpxu2xxmmd3ex
} yield ()
```

### Usage (sync)
```scala
import cats.Id
import me.wojnowski.scuid.Cuid2
import me.wojnowski.scuid.Cuid2Gen

...

val generator: Cuid2Gen[Id] = Cuid2Gen.unsafeDefault
val id: Cuid2               = generator.generate // r0o5ncoizclu1b9iraz620cn
```

## Length
There are three options for length of the generated ID:
1. `Cuid2` - 24 characters (default)
2. `Cuid2Long` - 32 characters
3. `Cuid2Custom[n]` - custom length (parametrised using literal types, e.g. `Cuid2Custom[10]` or `Cuid2Custom[27]`)

## Validation
```scala
val idDefault: Option[Cuid2]                = Cuid2.validate("r0o5ncoizclu1b9iraz620cn")
val idLong: Option[Cuid2Long]               = Cuid2Long.validate("tzwxyg5tav24zm8ycsrtpfi0njhegmes")
val idCustomLength: Option[Cuid2Custom[10]] = Cuid2Custom.validate[10]("axcf7v6n1w")
```

## TODO
- [ ] Circe integration
- [ ] Tapir integration
- [ ] ZIO integration
