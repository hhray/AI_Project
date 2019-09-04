#lang racket

(require "gsa.rkt")
(provide
 test-graph
 display-nodes)

(define test-graph (list (arc (node "ts") (node "mail") 6) 
                         (arc (node "mail") (node "ts") 6)
                         (arc (node "o103") (node "ts") 8) 
                         (arc (node "ts") (node "o103") 8) 
                         (arc (node "o103") (node "b3") 4)
                         (arc (node "o103") (node "o109") 12) 
                         (arc (node "o109") (node "o103") 12)
                         (arc (node "o109") (node "o119") 16) 
                         (arc (node "o119") (node "o109") 16) 
                         (arc (node "o109") (node "o111") 4) 
                         (arc (node "o111") (node "o109") 4) 
                         (arc (node "b1") (node "c2") 3)
                         (arc (node "b1") (node "b2") 6) 
                         (arc (node "b2") (node "b1") 6) 
                         (arc (node "b2") (node "b4") 3) 
                         (arc (node "b4") (node "b2") 3) 
                         (arc (node "b3") (node "b1") 4) 
                         (arc (node "b1") (node "b3") 4) 
                         (arc (node "b3") (node "b4") 7) 
                         (arc (node "b4") (node "b3") 7) 
                         (arc (node "b4") (node "o109") 7)
                         (arc (node "c1") (node "c3") 8) 
                         (arc (node "c3") (node "c1") 8)
                         (arc (node "c2") (node "c3") 6) 
                         (arc (node "c3") (node "c2") 6)
                         (arc (node "c2") (node "c1") 4) 
                         (arc (node "c1") (node "c2") 4) 
                         (arc (node "d2") (node "d3") 4) 
                         (arc (node "d3") (node "d2") 4) 
                         (arc (node "d1") (node "d3") 8) 
                         (arc (node "d3") (node "d1") 8)
                         (arc (node "o125") (node "d2") 2)
                         (arc (node "d2") (node "o125") 2) 
                         (arc (node "o123") (node "o125") 4)
                         (arc (node "o125") (node "o123") 4) 
                         (arc (node "o123") (node "r123") 4)
                         (arc (node "r123") (node "o123") 4)
                         (arc (node "o119") (node "o123") 9) 
                         (arc (node "o123") (node "o119") 9)
                         (arc (node "o119") (node "storage") 7) 
                         (arc (node "storage") (node "o119") 7)))


;; display-nodes : NodeList
;; EFFECT: display every node name in the node list
(define (display-nodes nodes)
  (for-each (lambda (node)
              (if (equal? node (last nodes))
                  (display (node-name node))
                  (display (format "~a->" (node-name node))))) nodes))


;; test : Graph NodeList Function Function SearchMode
;; EFFECT: if at least one path is found, display all paths, otherwise display "No paths found"
(define (test graph start-states goal-predicate search-strategy search-mode)
  (let ([result-paths (graph-search graph start-states goal-predicate search-strategy search-mode)])
    (if (empty? result-paths)
        (displayln "No paths found")
        (for-each (lambda (path)
                    (display "Path: ")
                    (display-nodes (path-nodes path))
                    (displayln "")
                    (displayln (format "Cost: ~a " (path-cost path))))
                    result-paths))
    (displayln "************************************")))



;; TESTS


;; graph-search : graph start-states goal-predicate search-strategy search-mode

(define test-goal (list "r123" "storage" "d1"))
(define test-ss (list (cons "dfs" dfs) (cons "bfs" bfs) (cons "depth-limited" depth-limited)
                      (cons "iterative-deepening" iterative-deepening) (cons "lowest-cost-first" lowest-cost-first)
                      (cons "heuristic" heuristic) (cons "best-first" best-first) (cons "a-star" a-star)))
(define test-sm (list "first" "all"))
(define test-cases (cartesian-product test-goal test-ss test-sm))

;; run all test cases
(for-each (lambda (case)
            (let ([goal (first case)]
                  [ss (second case)]
                  [sm (third case)])
               (displayln (format "Search Strategy: ~a, Search Mode: ~a, Goal: ~a\n" (car ss) sm goal))
               (test test-graph (list (node "o103")) (lambda (end-node) (equal? end-node (node goal))) (cdr ss) sm)))
             test-cases)