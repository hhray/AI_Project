#lang racket

(require "gsa.rkt")
(require "test.rkt")


;; SEARCH STRATEGIES


;; bfs
;; select the first path from the frontier
(define (bfs frontier)
  (first frontier))


;; dfs
;; select the last path from the frontier
(define (dfs frontier)
  (last frontier))



;; FUNCTIONS


;; extend-frontier : PathList Graph Function -> PathList
;; RETURNS: extend the given frontier
(define (extend-frontier frontier graph search-strategy)
  (let* ([current-path (search-strategy frontier)]
         [last-node (last current-path)]
         [after-frontier (remove current-path frontier)]
         [new-path-list (map (lambda (neighbor)
                               (append current-path (list neighbor)))
                             (unvisited-nodes (neighbors last-node graph) current-path))])
    (append after-frontier new-path-list)))


;; bidirectional-search : Node Node Graph Function -> Path
;; RETURNS: init start frontier and goal frontier, call bidirectional-search-helper 
(define (bidirectional-search start goal graph uninformed-search-strategy)
  (bidirectional-search-helper (list (list start)) (list (list goal)) graph uninformed-search-strategy))


;; bidirectional-search-helper : PathList PathList Graph Function -> Path
;; RETURNS: search the graph, return path
(define (bidirectional-search-helper start-frontier goal-frontier graph search-strategy)
  (if (or (empty? start-frontier) (empty? goal-frontier))
      '()
      (let ([intersect-nodes (intersect start-frontier goal-frontier)])
        (if (empty? intersect-nodes)
            (bidirectional-search-helper 
                                         (extend-frontier start-frontier graph search-strategy)
                                         (extend-frontier goal-frontier graph search-strategy)
                                         graph search-strategy) 
            (build-path start-frontier goal-frontier intersect-nodes)))))



;; HELPER FUNCTIONS


;; path-end-with : PathList Node -> Path
;; RETURNS: find the first partial path that ends with the given node
(define (path-end-with frontier node)
  (if (empty? frontier) '()
      (let* ([current-path (first frontier)]
             [node-index (index-of current-path node)])
        (if node-index
            (take current-path node-index)
            (path-end-with (rest frontier) node)))))


;; build-path : PathList PathList NodeList -> Path
;; RETURNS: build path from start frontier and goal frontier
(define (build-path start-frontier goal-frontier nodes)
  (let* ([intersect-node (first nodes)]
         [start-path (path-end-with start-frontier intersect-node)]
         [end-path (path-end-with goal-frontier intersect-node)])
    (append start-path (list intersect-node) (reverse end-path))))


;; nodes : PathList -> NodeList
;; RETURNS: all nodes occur in the paths, without duplicates
(define (nodes frontier)
  (remove-duplicates (foldr append '() frontier)))


;; intersect : PathList PathList -> NodeList
;; RETURNS: nodes occur in both paths
(define (intersect frontier-1 frontier-2)
  (filter (lambda (node) (member node (nodes frontier-2))) (nodes frontier-1)))



;; TESTS


;; test-bidir : String String Function
;; EFFECT: if a path is found, display that path, otherwise display "No path found"
(define (test-bidir start goal search-strategy)
  (let ([path (bidirectional-search (node start) (node goal) test-graph search-strategy)])
    (if (empty? path)
        (displayln "No path found")
        (begin 
          (display "Path: ")
          (display-nodes path)
          (displayln "")))
    (displayln "************************************")))



(define test-goal (list "r123" "storage" "d1"))
(define test-ss (list (cons "dfs" dfs) (cons "bfs" bfs)))
(define test-cases (cartesian-product test-goal test-ss))

;; run all test cases
(for-each (lambda (case)
            (let ([goal (first case)]
                  [ss (second case)])
               (displayln (format "Search Strategy: bidir-~a, Goal: ~a\n" (car ss) goal))
               (test-bidir "o103" goal (cdr ss))))
             test-cases)