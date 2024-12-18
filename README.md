# Dati e Algoritmi

## Programming Project 2024-25
**Version 4 - December 5, 2024**

This project requires you to implement a **Priority Queue** using a skip list, a probabilistic data structure introduced in 1989 by William Pugh for the efficient implementation of the Map ADT [1].

---

## 1. Skip Lists

### Skip list structure
Let `M` be a collection of (key, value) pairs with distinct keys. A skip list `S` for `M` consists in a series of lists `{S₀, S₁, ..., Sₕ}`, with `h ≥ 1`, which satisfy the following properties:

- **Order:** Each `Sᵢ` is sorted in increasing order and is delimited by two sentinels with keys `-∞` and `+∞`, respectively.
- **Content:** `S₀` contains all elements of `M` and the two sentinels.
- **Subsets:** For every `0 ≤ i < h`, the entries in `Sᵢ₊₁` are a subset of those in `Sᵢ`. Each element `e ∈ Sᵢ` appears in `Sᵢ₊₁` with probability `α` (typically `α = 0.5`).
- **Top level:** `Sₕ` contains only the two sentinels.
- **Connectivity:** For every entry `e`, including the sentinels, the positions containing its occurrences in `{S₀, S₁, ..., Sₕ}` are connected to form a vertical list.

The data structure can be visualized as a two-dimensional collection of positions threaded both vertically and horizontally, accessible through its top-left position `s`, called the **start position**. An example is shown in **Figure 1**.

---

### 1.1 Search for a key `k`

Searching for a given key `k` locates the position `p ∈ S₀` containing the largest key `≤ k`. The algorithm, **SkipSearch(k)**, starts at `s` and, for each list `Sᵢ` (`i = h, h-1, ..., 0`), moves to the largest key `≤ k` in `Sᵢ`. If `i > 0`, it moves to the position `below(p)` in `Sᵢ₋₁`; if `i = 0`, it stops and returns `p`. Examples are shown in **Figures 2** and **3**.

---

### 1.2 Insert an entry with key `k`

To insert an entry `e` with key `k` into `S`:
1. Run **SkipSearch(k)**.
2. Let `pᵢ` be the rightmost position in `Sᵢ` reached by SkipSearch.
3. If `p₀` contains an entry with key `k`, replace it with the new entry `e`.
4. Otherwise, insert `e` at new positions `{q₀, ..., qₗ}`, where `ℓ` is determined by a sequence of independent coin tosses until the first tail appears. Example: if the sequence is `heads, heads, heads, tail`, then `ℓ = 3`.

**Figure 4** illustrates an insertion example.

---

### 1.3 Efficiency of skip list methods

When `α ≤ 1/2`, the complexity of search, insertion, and deletion methods is `O(log n)`, and the total number of nodes among all lists is `O(n)`. The value of `α` regulates a space-time trade-off: as `α` increases, space usage increases, and runtime decreases. See [1, Section 10.4] for details.

---

## 2. Assignment for 2 points

You must implement a **Priority Queue** using a skip list. Specifically, design the following classes:

### 2.1 `MyEntry` class
Instances are `(Integer, String)` pairs representing the entries.

### 2.2 `SkipListPQ` class
Instances are skip lists containing entries of class `MyEntry`. The class includes the following methods:
- `size()`: Returns the number of entries.
- `min()`: Returns the entry with the smallest key and prints its key and value.
- `insert(key, string)`: Inserts a new entry.
- `removeMin()`: Removes and returns the entry with the smallest key, updating the skip list levels as necessary.
- `print()`: Prints all entries in increasing key order with their vertical list sizes.

### 2.3 `TestProgram` class
This program executes operations specified in an input file. It reads parameters `N` (number of operations) and `α` (head probability) from the file, initializes an empty `SkipListPQ`, and executes the specified operations.

---

## 3. Assignment for 3 points

To obtain full marks:
- Modify `insert(key, string)` to return the number of nodes traversed during insertion.
- After execution, the program prints:
  - `α`
  - Number of entries
  - Total number of insert operations
  - Average number of nodes traversed per insert.

Run `TestProgram` with input files from the provided `alphaEfficiencyTest.zip` archive, recording the results for each file.

---

## 4. Deliverables

Submit `TestProgram.java` containing all classes. The code must compile and run using:
```bash
javac TestProgram.java
