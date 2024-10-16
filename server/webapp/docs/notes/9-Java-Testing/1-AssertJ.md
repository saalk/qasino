Back to [Index](0-index.md)

# Testing
// Victor Rentea DEVOXX 2024

// Use assertThat !!
With this version, there is no confusion; everything is crystal clear. It also reads more like a sentence: “Assert that the actual value is equal to the expected value 100.” assertFalse(expected

Why
1. Prove of working


## Parameterized test
If all the tests are data you should parameterize it
Can prevent carthesian explosion
But some combinations are not business wize
```java
@ParameterizedTest
@ValueSource(strings = {"", "  "})
void isBlank_ShouldReturnTrueForNullOrBlankStrings(String input) {
    assertTrue(Strings.isBlank(input));
}
```

