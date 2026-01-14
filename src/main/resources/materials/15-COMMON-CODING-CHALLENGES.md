# Common Coding Challenges - Complete Guide

## Overview
A practical, interview-focused guide to common coding challenges. Covers core data structures, algorithm patterns, problem-solving strategies, complexity analysis, and Java examples. Includes a Common Questions section and a quick reference.

---

## 1) Data Structures

### Arrays & Strings
- Fixed-size sequential storage; constant-time index access.
- Common tasks: reversing, rotation, sliding window, two pointers.

### Linked Lists
- Nodes with next (and prev for doubly-linked); sequential access.
- Common tasks: cycle detection (Floyd), reverse, merge, k-th from end.

### Stacks & Queues
- Stack: LIFO (push/pop); used for parsing, backtracking, undo.
- Queue: FIFO (offer/poll); used for BFS, producer-consumer.

### Hash Tables
- Key-value storage with O(1) average lookups.
- Common tasks: frequency count, deduplication, two-sum.

### Trees & Graphs
- Trees: hierarchical; binary trees, BSTs.
- Graphs: nodes/edges; represented via adjacency list/matrix.
- Common tasks: DFS/BFS, shortest path, topological sort, cycle detection.

---

## 2) Algorithm Patterns

### Two Pointers
- Move pointers from ends or within window.
- Use cases: palindrome check, pair sum, remove duplicates.

### Sliding Window
- Maintain a window with start/end indexes.
- Use cases: longest substring, max subarray sum, fixed-size window stats.

### Binary Search
- Divide search space by halves; requires monotonic property.
- Use cases: search in sorted array, find boundaries, rotated arrays.

### Divide & Conquer
- Break into subproblems; combine results.
- Use cases: merge sort, quick sort, closest pair.

### Greedy
- Local optimal choices aiming for global optimum.
- Use cases: interval scheduling, coin change (canonical), Huffman coding.

### Dynamic Programming (DP)
- Optimal substructure + overlapping subproblems; memoization/tabulation.
- Use cases: knapsack, LIS, edit distance, coin change (general), grid paths.

### Backtracking
- Explore choices; revert on failure; prune with constraints.
- Use cases: combinations, permutations, N-Queens, Sudoku.

---

## 3) Complexity Analysis

### Big-O
- Time: O(1), O(log n), O(n), O(n log n), O(n^2).
- Space: memory used by algorithm; trade-offs between time and space.

### Tips
- Identify dominant operations.
- For nested loops, multiply complexities.
- For divide and conquer, use Master theorem when applicable.

---

## 4) Essential Java Utilities for Interviews

- Arrays.sort, Collections.sort, PriorityQueue, HashMap/HashSet, LinkedList/ArrayDeque.
- StringBuilder for string manipulations.
- Comparator for custom sorts.

---

## 5) Common Problems & Java Solutions

### Reverse a String
```java
public String reverse(String s) {
    return new StringBuilder(s).reverse().toString();
}
```

### Check Palindrome (Two Pointers)
```java
public boolean isPalindrome(String s) {
    int i = 0, j = s.length() - 1;
    while (i < j) {
        if (s.charAt(i++) != s.charAt(j--)) return false;
    }
    return true;
}
```

### Two Sum (Hash Map)
```java
public int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> idx = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
        int need = target - nums[i];
        if (idx.containsKey(need)) return new int[]{idx.get(need), i};
        idx.put(nums[i], i);
    }
    return new int[]{-1, -1};
}
```

### Valid Parentheses (Stack)
```java
public boolean isValid(String s) {
    Deque<Character> st = new ArrayDeque<>();
    Map<Character, Character> pairs = Map.of(')', '(', '}', '{', ']', '[');
    for (char c : s.toCharArray()) {
        if (pairs.containsValue(c)) st.push(c);
        else if (pairs.containsKey(c)) {
            if (st.isEmpty() || st.pop() != pairs.get(c)) return false;
        }
    }
    return st.isEmpty();
}
```

### Merge Two Sorted Lists (Linked List)
```java
class ListNode { int val; ListNode next; ListNode(int v){val=v;} }
public ListNode mergeTwoLists(ListNode a, ListNode b) {
    ListNode dummy = new ListNode(0), cur = dummy;
    while (a != null && b != null) {
        if (a.val <= b.val) { cur.next = a; a = a.next; }
        else { cur.next = b; b = b.next; }
        cur = cur.next;
    }
    cur.next = (a != null) ? a : b;
    return dummy.next;
}
```

### Maximum Subarray (Kadane)
```java
public int maxSubArray(int[] nums) {
    int best = nums[0], cur = nums[0];
    for (int i = 1; i < nums.length; i++) {
        cur = Math.max(nums[i], cur + nums[i]);
        best = Math.max(best, cur);
    }
    return best;
}
```

### Longest Substring Without Repeating Characters (Sliding Window)
```java
public int lengthOfLongestSubstring(String s) {
    int[] last = new int[256]; Arrays.fill(last, -1);
    int best = 0, start = 0;
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (last[c] >= start) start = last[c] + 1;
        last[c] = i;
        best = Math.max(best, i - start + 1);
    }
    return best;
}
```

### Binary Search
```java
public int binarySearch(int[] nums, int target) {
    int l = 0, r = nums.length - 1;
    while (l <= r) {
        int m = l + (r - l) / 2;
        if (nums[m] == target) return m;
        if (nums[m] < target) l = m + 1; else r = m - 1;
    }
    return -1;
}
```

### BFS (Graph)
```java
public int shortestPath(List<List<Integer>> graph, int src, int dst) {
    int n = graph.size();
    int[] dist = new int[n]; Arrays.fill(dist, -1);
    Deque<Integer> q = new ArrayDeque<>();
    dist[src] = 0; q.offer(src);
    while (!q.isEmpty()) {
        int u = q.poll();
        for (int v : graph.get(u)) {
            if (dist[v] == -1) { dist[v] = dist[u] + 1; q.offer(v); }
        }
    }
    return dist[dst];
}
```

### DFS (Recursive)
```java
public void dfs(int u, List<List<Integer>> g, boolean[] seen) {
    if (seen[u]) return; seen[u] = true;
    for (int v : g.get(u)) dfs(v, g, seen);
}
```

### Topological Sort (Kahn's Algorithm)
```java
public List<Integer> topo(int n, List<List<Integer>> g) {
    int[] indeg = new int[n];
    for (int u = 0; u < n; u++) for (int v : g.get(u)) indeg[v]++;
    Deque<Integer> q = new ArrayDeque<>();
    for (int i = 0; i < n; i++) if (indeg[i] == 0) q.offer(i);
    List<Integer> order = new ArrayList<>();
    while (!q.isEmpty()) {
        int u = q.poll(); order.add(u);
        for (int v : g.get(u)) if (--indeg[v] == 0) q.offer(v);
    }
    return order.size() == n ? order : List.of(); // cycle if not all visited
}
```

### Dijkstra's Algorithm (Shortest Path)
```java
public int[] dijkstra(int n, List<List<int[]>> g, int src) {
    int[] dist = new int[n]; Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0});
    while (!pq.isEmpty()) {
        int[] cur = pq.poll(); int u = cur[0], d = cur[1];
        if (d != dist[u]) continue;
        for (int[] e : g.get(u)) { int v = e[0], w = e[1];
            if (dist[u] + w < dist[v]) { dist[v] = dist[u] + w; pq.offer(new int[]{v, dist[v]}); }
        }
    }
    return dist;
}
```

### Edit Distance (DP)
```java
public int editDistance(String a, String b) {
    int n = a.length(), m = b.length();
    int[][] dp = new int[n+1][m+1];
    for (int i = 0; i <= n; i++) dp[i][0] = i;
    for (int j = 0; j <= m; j++) dp[0][j] = j;
    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
            int cost = a.charAt(i-1) == b.charAt(j-1) ? 0 : 1;
            dp[i][j] = Math.min(
                Math.min(dp[i-1][j] + 1, dp[i][j-1] + 1),
                dp[i-1][j-1] + cost
            );
        }
    }
    return dp[n][m];
}
```

### Knapsack (0/1 DP)
```java
public int knapsack(int[] w, int[] v, int cap) {
    int n = w.length; int[][] dp = new int[n+1][cap+1];
    for (int i = 1; i <= n; i++) {
        for (int c = 0; c <= cap; c++) {
            dp[i][c] = dp[i-1][c];
            if (w[i-1] <= c) dp[i][c] = Math.max(dp[i][c], dp[i-1][c-w[i-1]] + v[i-1]);
        }
    }
    return dp[n][cap];
}
```

### Generate Combinations (Backtracking)
```java
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> res = new ArrayList<>();
    backtrack(1, n, k, new ArrayList<>(), res);
    return res;
}
private void backtrack(int start, int n, int k, List<Integer> cur, List<List<Integer>> res) {
    if (cur.size() == k) { res.add(new ArrayList<>(cur)); return; }
    for (int i = start; i <= n; i++) {
        cur.add(i);
        backtrack(i + 1, n, k, cur, res);
        cur.remove(cur.size() - 1);
    }
}
```

---

## 6) Problem-Solving Process

### Steps
- Clarify requirements; restate the problem.
- Identify constraints (time/space, inputs, outputs).
- Choose data structures and algorithm patterns.
- Start with a simple solution; iterate to optimize.
- Test with edge cases; reason about complexity.

### Edge Cases
- Empty inputs; single-element arrays.
- Negative numbers; duplicates; overflow.
- Large inputs; degenerate graphs.

---

## 7) Common Interview Questions & Answers

### Q1: How do you choose between data structures?
- Based on operations: indexing (array), frequent insert/delete (linked list), fast lookups (hash map), ordering (tree), FIFO/LIFO (queue/stack).

### Q2: When is binary search applicable?
- When the search space has a monotonic property (sorted or can be transformed to monotonic). Also for boundary finding (first/last occurrence).

### Q3: How do you detect cycles in a linked list or graph?
- Linked list: Floyd's Tortoise and Hare.
- Graph: DFS with visited + recursion stack, or Kahn's algorithm for DAG detection.

### Q4: What’s the difference between BFS and DFS?
- BFS explores level by level (shortest path in unweighted graphs); DFS dives deep (useful for connectivity, topological sort, cycle detection).

### Q5: How do you optimize time vs space?
- Use in-place algorithms, trade memory for speed (hashing), precompute with DP, compress states, and avoid unnecessary allocations.

### Q6: How to approach DP problems?
- Identify subproblems and recurrence; choose memoization (top-down) or tabulation (bottom-up); define base cases; ensure overlapping subproblems.

### Q7: What are common pitfalls?
- Off-by-one errors, not handling empty cases, missing edge conditions, incorrect loop bounds, failing to reset state.

### Q8: How do you test your solution?
- Unit tests for typical, edge, and extreme cases; assert expected outputs; analyze complexity.

---

## Quick Reference
- Patterns: two pointers, sliding window, binary search, greedy, DP, backtracking.
- Data structures: arrays, linked lists, stacks/queues, hash maps, trees/graphs.
- Practice: LeetCode Easy/Medium; focus on reasoning and clarity.

---

*Last Updated: January 2026*

