# GSA framework


This doc contains three racket files. 

`gsa.rkt`: the GSA framework. The main function is `graph-search`. It takes five arguments: `graph`, `start-states`, `goal-predicate`, `search-strategy`, `search-mode`. The search framework can take any search strategy that implements the SearchStrategy structure. Eight hardcoded search strategies are provided: `bfs`, `dfs`, `lowest-cost-first`, `depth-limited`, `iterative-deepening`, `heuristic`, `best-first`, `a-star`.	

`bidir.rkt`: bidirectional search. The main function is `bidirectional-search`. It takes four arguments: `start`, `goal`, `graph`, `uninformed-search-strategy`. The bidirectional search framework currently supports two uninformed search strategies: `bfs` and `dfs`. It will return a path as soon as the start frontier intersects with the goal frontier. This file also contains tests for the bidirectional search framework.

`test.rkt`: tests for the GSA framework. The test cases cover all three given goals, eight hardcoded search strategies and two search modes. 

## Getting Started
To get started you can simply run the `gsa.rkt` and the `bidir.rkt`. You can run `graph-search` and `bidirectional-search` with different arguments and test the results.

## Running the tests
To test you can simply run the `test.rkt` and the `bidir.rkt`. It will print something like this:

> Search Strategy: best-first, Search Mode: first, Goal: d1
> 
> Path: o103->o109->o119->o123->o125->d2->d3->d1
> 
> Cost: 55

## Bonus part
1. Implement all eight search strategies without using any loops.
2. Implement bidirectional search.


NOTE 1.Bidirectional search test is in the `bidir.rkt`, all the other search tests are in `test.rkt`
     2.For the two search strategies ---`depth-limited`, `iterative-deepening`, the MAX-DEPTH-LIMIT were hard-coded in function. To test
      with different MAX-DEPTH-LIMIT, please change it in function.

