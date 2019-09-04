#lang racket

(provide
 node
 node-name
 arc
 path-nodes
 path-cost
 ss
 bfs
 dfs
 lowest-cost-first
 depth-limited
 iterative-deepening
 heuristic
 best-first
 a-star
 neighbors
 unvisited-nodes
 graph-search)



;; DATA DEFINITIONS

;; Node

;; A Node is represented as a struct (node name)
;; with the field
;; name  : String represents the name of the node

(struct node (name) #:transparent)


;; NodeList
;; A NodeList is represented as a list of Node


;; Arc

;; An Arc is represented as a struct (arc from to cost)
;; with the fields
;; from  : Node represents the from node
;; to    : Node represents the to node
;; cost  : Number represents the cost

(struct arc (from to cost) #:transparent)


;; Path

;; A Path is represented as a struct (path nodes cost)
;; with the fields
;; nodes : NodeList represents the node list in the path
;; cost  : Number   represents the cost of the path

(struct path (nodes cost) #:transparent)


;; PathList
;; A PathList is represented as a list of Path


;; Graph
;; A Graph is represented as a list of Arc


;; SearchStrategy

;; A SearchStrategy is represented as a struct (ss select extend end)
;; with the fields
;; select : Function represents the strategy that selects path from frontier
;; extend : Function represents the strategy that extends the frontier
;; end    : Function represents the strategy that deals with empty frontier

(struct ss (select extend end))


;; SearchMode
;; A SearchMode is represented as one of the following:
;;      -- "first"
;;      -- "all"



;; FUNCTIONS


;; GSA framework

;; graph-search : Graph NodeList Function SearchStrategy SearchMode -> PathList
;; RETURNS: init result paths and frontier, call graph-search-helper
(define (graph-search graph start-states goal-predicate search-strategy search-mode)
  (graph-search-helper '() (init-frontier start-states) graph start-states goal-predicate search-strategy search-mode))


;; graph-search-helper : PathList PathList Graph NodeList Function SearchStrategy SearchMode -> PathList
;; RETURNS: search the graph and return paths
(define (graph-search-helper paths frontier graph start-states goal-predicate search-strategy search-mode)
  (if (empty? frontier)
      ((ss-end search-strategy) paths frontier graph start-states goal-predicate search-strategy search-mode)
      (let* ([current-path ((ss-select search-strategy) frontier graph goal-predicate)]
             [last-node (last (path-nodes current-path))]
             [after-frontier (remove current-path frontier)]
             [extended-frontier ((ss-extend search-strategy) current-path last-node graph after-frontier)]
             [reach-goal (goal-predicate last-node)])
        (if (and reach-goal (first-solution? search-mode))
            (list current-path)
            (graph-search-helper
             (if reach-goal (append paths (list current-path)) paths) 
             extended-frontier graph start-states goal-predicate search-strategy search-mode)))))




;; HELPER FUNCTIONS


;; init-frontier : NodeList -> PathList
;; RETURNS: create the initial frontier with given nodes
(define (init-frontier nodes)
  (map (lambda (node) (path (list node) 0)) nodes))


;; extend-frontier : Path Node Graph PathList -> PathList
;; RETURNS: default extend strategy, find neighbors of the given node,
;;     remove the visited ones, append it to the current path
(define (extend-frontier current-path node graph frontier)
  (let ([new-path-list (map (lambda (neighbor)
                              (let ([cost (find-cost node neighbor graph)])
                                (path (append (path-nodes current-path) (list neighbor)) (+ (path-cost current-path) cost))))
                            (unvisited-nodes (neighbors node graph) (path-nodes current-path)))])
    (append frontier new-path-list)))


;; find-cost : Node Node Graph -> Number
;; RETURNS: the cost from node one to node two
(define (find-cost from-node to-node graph)
  (arc-cost (findf (lambda (current-arc)
                     (and (equal? (arc-from current-arc) from-node)
                          (equal? (arc-to current-arc) to-node)))
                   graph)))
                   

;; neighbors : Node Graph -> NodeList
;; RETURNS: neighbors of the given node in the graph
(define (neighbors node graph)
  (foldl (lambda (arc lst)
           (if (equal? (arc-from arc) node)
               (append lst (list (arc-to arc)))
               lst))
         '() graph))


;; last-node : Path -> Node
;; RETURNS: last node on the given path
(define (last-node path)
  (last (path-nodes path)))


;; unvisited-nodes : NodeList NodeList -> NodeList
;; RETURNS: only keep the unvisited nodes
(define (unvisited-nodes nodes visited)
  (filter (lambda (node) (not (member node visited))) nodes))


;; first-solution? : SearchMode -> Boolean
;; RETURNS: whether only return the first solution
(define (first-solution? search-mode)
  (string=? search-mode "first"))


;; all-solutions? : SearchMode -> Boolean
;; RETURNS: whether return all solutions
(define (all-solutions? search-mode)
  (string=? search-mode "all"))


;; return-paths : PathList PathList Graph NodeList Function SearchStrategy SearchMode -> PathList
;; RETURNS: default strategy to handle the empty frontier, return the given paths
(define (return-paths paths frontier graph start-states goal-predicate search-strategy search-mode)
  paths)


;; heuristic-function : Node -> Number
;; RETURNS: heuristic value for the given node
(define (heuristic-function node)
  0)


;; path-heuristic-value : Path -> Number
;; RETURNS: heuristic value of the given path
(define (path-heuristic-value path)
  (heuristic-function (last-node path)))


;; best-first-heuristic-function : PathList Graph NodeList Function -> Number
;; RETURNS: heuristic function for the best first strategy, return heuristic value
(define (best-first-heuristic-function frontier graph start-states goal-predicate)
  (let ([lowest-cost-first-paths (graph-search graph start-states goal-predicate lowest-cost-first "first")])
    (if (empty? lowest-cost-first-paths) 0
        (/ (path-cost (first lowest-cost-first-paths)) 2))))



;; SEARCH STRATEGIES


;; bfs
;; select strategy: select the first path from the frontier
(define bfs
  (ss (lambda (frontier graph goal-predicate)
        (first frontier))
      extend-frontier
      return-paths))


;; dfs
;; select strategy: select the last path from the frontier
(define dfs
  (ss (lambda (frontier graph goal-predicate)
        (last frontier))
      extend-frontier
      return-paths))


;; lowest-cost-first
;; select strategy: select the path with the lowest cost
(define lowest-cost-first
  (ss (lambda (frontier graph goal-predicate)
        (let ([lowest-cost (apply min (map path-cost frontier))])
          (findf (lambda (path)
                   (equal? (path-cost path) lowest-cost))
                 frontier)))
      extend-frontier
      return-paths))


;; depth-limited
;; select strategy: same as dfs
;; extend strategy: when the depth of the current path is
;;     larger or equal to the max depth limit, stop extending the frontier
(define depth-limited
  (let ([MAX-DEPTH-LIMIT 3])
    (ss (ss-select dfs)
        (lambda (current-path node graph frontier)
          (if (>= (length (path-nodes current-path)) MAX-DEPTH-LIMIT)
              frontier
              (extend-frontier current-path node graph frontier)))
        return-paths)))


;; iterative-deepening
;; select strategy: same as dfs
;; extend strategy: same as depth-limited
;; end strategy: if the bound is hitted and the current limit is less than
;;     max depth limit, run graph search again with bigger limit, otherwise
;;     return paths
(define iterative-deepening
  (let ([MAX-DEPTH-LIMIT 20]
        [current-limit 1]
        [hit-bound #f])
    (ss (ss-select dfs)
        (lambda (current-path node graph frontier)
          (if (>= (length (path-nodes current-path)) current-limit)
              (begin (set! hit-bound #t)
                     frontier)
              (extend-frontier current-path node graph frontier)))
        (lambda (paths frontier graph start-states goal-predicate search-strategy search-mode)
          (if (and hit-bound (< current-limit MAX-DEPTH-LIMIT))
              (begin (set! current-limit (add1 current-limit))
                     (set! hit-bound #f)
                     (graph-search graph start-states goal-predicate search-strategy search-mode))
              paths)))))


;; heuristic
;; select strategy: select the path with the lowest heuristic value
(define heuristic
  (ss (lambda (frontier graph goal-predicate)
        (let ([lowest-heuristic-value (apply min (map path-heuristic-value frontier))])
          (findf (lambda (path)
                   (equal? (heuristic-function (last-node path)) lowest-heuristic-value))
                 frontier)))
      extend-frontier
      return-paths))


;; best-first
;; select strategy: select the path with the lowest heuristic value according to
;;     the heuristic function for the best-first strategy
(define best-first
  (ss (lambda (frontier graph goal-predicate)
        (let ([best-first-lowest-heuristic-value
               (apply min (map (lambda (path)
                                 (best-first-heuristic-function
                                  frontier graph (list (last-node path)) goal-predicate)) frontier))])
          (findf (lambda (path)
                   (equal? (best-first-heuristic-function frontier graph (list (last-node path)) goal-predicate)
                           best-first-lowest-heuristic-value))
                 frontier)))
      extend-frontier
      return-paths))
      

;; a-star
;; select strategy: select the path with the least sum of the cost and the heuristic value
(define a-star
  (ss (lambda (frontier graph goal-predicate)
        (let ([lowest (apply min (map + (map path-cost frontier) (map path-heuristic-value frontier)))])
          (findf (lambda (path)
                   (equal? (+ (path-cost path) (heuristic-function (last-node path))) lowest))
                 frontier)))
      extend-frontier
      return-paths))