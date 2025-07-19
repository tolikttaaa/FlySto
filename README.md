# Test problem for [FlySto](https://www.flysto.net/home) interview

## Task: Flight Path Simplification
Given a list of many points (typically thousands) representing a recorded flight path, where each point is sampled at approximately one point per second, identify and remove unnecessary points to simplify the path.

### Requirements
- The original path may contain long, straight segments.
- The simplified path must **never deviate** from the original path by **more than a given distance**.
- The goal is to **reduce the number of points** in the path.
- The solution **does not need to produce the minimal number of points**, but it should be **reasonably fast** to compute.

### Objective
Develop an algorithm that approximates the original flight path using fewer points while maintaining a bounded error (maximum deviation) and ensuring fast computation time.

## Solution
The solution to this task is described in a separate document: [PathSimplifier](PathSimplifier/README.md)
