# FlySto Path Simplifier

This module provides implementations for geographic path simplification algorithms
designed to reduce the number of points in a flight path while preserving its overall shape.
It supports two main algorithms: **Greedy** and **Douglas-Peucker**.
It also can be extended by other algorithms, that can be easily added to the repository.

## Algorithms
### 1. Greedy Simplifier
A simple algorithm that extends each segment as far as possible until deviation exceeds the threshold.

**Time Complexity:** `O(n^2)`  
**Space Complexity:** `O(n)`

**Pros:**
- Easy to implement.
- Fast on small datasets.

**Cons:**
- Less efficient for large datasets.
- May yield suboptimal results.

---

### 2. Douglas-Peucker Simplifier
A recursive divide-and-conquer algorithm that selects points based on their perpendicular distance from line segments.

**Time Complexity:**
- Average: `O(n log n)`
- Worst: `O(n²)`

**Space Complexity:**
- Average: `O(log n)` recursion depth
- Worst: `O(n)`

**Pros:**
- High-quality simplification.
- Well-suited for complex paths.

**Cons:**
- Recursion may affect performance on huge paths.
- Slightly more complex. 
- Maybe slower than greedy for short paths.
