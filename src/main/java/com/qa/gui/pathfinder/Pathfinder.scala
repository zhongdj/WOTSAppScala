package com.qa.gui.pathfinder

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.PriorityQueue

/**
 * @author cboucher
 */
class Pathfinder {

  val vertexList = new ListBuffer[Vertex]
  
  val map = Array(
    Array(2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 2),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1),
    Array(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
    Array(2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2))
  var outputMap = map
  var vMap = vertexMap

  // Fill the vertex map with new vertexes
  def vertexMap(): Array[Array[Vertex]] = {

    var vertexMap: Array[Array[Vertex]] = Array.ofDim[Vertex](map.length, map(0).length)

    def row(x: Int) {
      def column(y: Int) {
        if (y < map(0).length) {
          vertexMap(x)(y) = new Vertex("X: " + x + "Y: " + y)
          column(y + (1))
        }
      }
      if (x < map.length) {
        column(0)
        row(x + (1))
      }
    }

    row(0)
    vertexMap
  }

  // Add edges to all vertexes in the map
  def fillMap(): Unit = {
    def row(x: Int) {
      def column(y: Int) {
        if (y < map(0).length) {
          // Create a new list of edges
          var edgeList = new MutableList[Edge]

          // If the map tile is not a wall
          if (map(x)(y) != 1) {

            // If the map tile is not on the left edge
            if (x != 0) {

              // If the map tile to the left is not a wall
              if (map(x - (1))(y) != 1) {

                // Add a new edge to the edge list
                val outputEdge = new Edge(vMap(x - 1)(y), 1)
                edgeList.+=(outputEdge)
              }
            }

            // If the map tile is not on the top edge
            if (y != 0) {

              // If the map tile above is not a wall
              if (map(x)(y - 1) != 1) {

                // Add a new edge to the edge list
                val outputEdge = new Edge(vMap(x)(y - 1), 1)
                edgeList.+=(outputEdge)
              }
            }

            // If the map tile is not on the right edge
            if (x < 11) {

              // If the map tile to the right is not a wall
              if (map(x + 1)(y) != 1) {

                // Add a new edge to the edge list
                val outputEdge = new Edge(vMap(x + 1)(y), 1)
                edgeList.+=(outputEdge)
              }
            }

            // If the map tile is not on the bottom edge
            if (y < map(0).length) {

              // If the map tile below is not a wall
              if (map(x)(y + 1) != 1) {

                // Add a new edge to the edge list
                val outputEdge = new Edge(vMap(x)(y + 1), 1)
                edgeList.+=(outputEdge)
              }
            }
          }

          // If the edge list isn't empty
          if (edgeList != null) {

            // Convert the list to an array
            var edgeArray = new Array[Edge](edgeList.size)

            // For each edge in the list
            def loop(z: Int) {
              if (z < edgeArray.size) {
                edgeArray(z) = edgeList(z)
                loop(z + (1))
              }
            }
            loop(0)

            // Add the edges to the vertex
            vMap(x)(y).adjacencies = edgeArray
          }
          column(y + (1))
        }
      }
      if (x < map.length) {
        column(0)
        row(x + (1))
      }
    }
    row(0)
  }

  /**
   * Performs the pathfinder algorithm on the vertex list and returns a map with the paths on it.
   */
  def pathfind(input: Array[ListBuffer[Int]]): Array[Array[Int]] = {

    // Add a location to the vertex list
    def loop(n: Int) {
      if (n < input.length) {
        // Add a vertex to the list
        println("hello")
        println(input(n)(0))
        println(input(n)(1))
        vertexList.+=(vMap(input(n)(0))(input(n)(1)))
        loop(n + (1))
      }
    }
    loop(0)

    /**
     *  Find the nearest vertex and call recursively
     */
    def pathFind(source: Vertex): Unit = {

      // Reset values
      var minDistance = Double.PositiveInfinity
      source.previous = null
      var nextVertexName: String = null
      var nextVertex: Vertex = null

      // Find all paths from the current vertex
      computePaths(source)

      def loop(i: Int) {
        // If the vertex is not the current vertex
        if (!vertexList(i).equals(source)) {
          System.out.println("Distance to " + vertexList(i) + ": " + vertexList(i).minDistance)

          // Check the distance is closer than the closest
          if (vertexList(i).minDistance < minDistance) {

            // Set the vertex to be the closest
            minDistance = vertexList(i).minDistance
            nextVertex = vertexList(i)
            nextVertexName = vertexList(i).name
          }
        }
      }
      // For each vertex in the vertex list
      loop(0)

      // Create a path of coordinates from the path finder
      var path = getShortestPathTo(nextVertex)

      // Draw the path on the warehouse map
      addPath(0, path)

      // Remove the vertex from the list
      vertexList -= source

      // Reset the vertex map
      vMap = vertexMap
      fillMap

      def setTiles(z: Int) {
        def row(x: Int) {
          if (x < map.length) {
            def col(y: Int) {
              if (y < map(0).length) {
                if (vertexList(z).name.equals(vMap(x)(y).name)) {
                  // Set it to the map tile
                  vertexList(z) = vMap(x)(y)
                }
                col(y + (1))
              }
            }
            col(0)
            row(x + (1))
          }
        }
        if (z < vertexList.size) {
          row(0)
          if (nextVertexName.equals(vertexList(z).name))

            // Set the next vertex to the vertex
            nextVertex = vertexList(z)

          setTiles(z + (1))
        }
      }

      setTiles(0)

      // If there more than one vertex left
      if (vertexList.size > 1)

        // Do it again using the target vertex
        pathFind(nextVertex)
    }

    /**
     *  Path finder using dijkstra algorithm
     */
    def computePaths(source: Vertex): Unit = {

      // Set the minimum distance of the source to be 0
      source.minDistance = 0

      // Add the source to a new queue
      var vertexQueue = new PriorityQueue[Vertex]()
      vertexQueue.+=(source)

      /**
       *  While the vertex queue is not empty
       */
      def whileLoop() {
        if (!vertexQueue.isEmpty) {

          // Poll the vertex queue
          val u = vertexQueue.head
          vertexQueue.dequeue

          /**
           *  For each edge of the polled vertex
           */
          def compare(i: Int) {
            if (i < u.adjacencies.size) {
              // Get the target vertex of the edge
              var v = u.adjacencies(i).target

              // Calculate the distance
              val distanceThroughU = u.minDistance + u.adjacencies(i).weight

              // If the distance is less than the minimum
              if (distanceThroughU < v.minDistance) {

                // Remove the target from the queue
                var tempQueue = vertexQueue.clone
                vertexQueue.dequeueAll

                def removeElement() {
                  if (!tempQueue.isEmpty) {
                    var next = tempQueue.dequeue
                    if (next != v) {
                      vertexQueue.+=(next)
                    }
                    removeElement
                  }
                }

                removeElement
                // Set the minimum distance of v to be the distance
                v.minDistance = distanceThroughU

                // Set the previous vertex of v to be u
                v.previous = u

                // Add v to the queue
                vertexQueue.+=(v)
              }
              compare(i + (1))
            }
          }
          whileLoop
        }
      }
    }

    /**
     *  Get the shortest path to the target vertex
     */
    def getShortestPathTo(target: Vertex): MutableList[Array[Int]] = {

      // Initialise variables
      var path = new MutableList[Vertex]
      var output = new MutableList[Array[Int]]

      // Iterate the loop by navigating the previous vertexes of each vertex
      // in the path
      def loop(vertex: Vertex) {

        // For each row in the map
        def row(x: Int) {

          // For each column in the map
          def col(y: Int) {
            if (y < map(0).length) {

              // If the current vertex is the same as the map vertex
              if (vMap(x)(y).name.equals(vertex.name)) {

                // Get the coordinates of the vertex
                val coordinates = Array(x, y)

                // Add the coordinates to the path
                output.+=(coordinates)
              }
              col(y + (1))
            }
          }
          if (x < map.length) {
            col(0)
            row(x + (1))
          }
        }
        if (vertex != null) {
          row(0)
          loop(vertex.previous)
        }
      }

      loop(target)

      // Reverse the path
      output.reverse
    }

    // Add a path to the map
    def addPath(i: Int, path: MutableList[Array[Int]]): Unit = {

      // For coordinate set in the array list
      if (i < path.size) {

        // Change the number on the map to 3 if its on the floor
        if (outputMap(path(i)(0))(path(i)(1)) == 0) {
          outputMap(path(i)(0))(path(i)(1)) = 3
        } else {
          // Change the number on the map to 4 if its on the shelf
          outputMap(path(i)(0))(path(i)(1)) = 4
        }
        addPath(i + (1), path)
      }
    }

    pathFind(vertexList(0))
    outputMap
  }
}