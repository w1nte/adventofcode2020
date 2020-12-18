import java.io.File
import kotlin.collections.ArrayList

const val CHUNK_SIZE = 40;

class World {
    fun nextChunk(chunk: Chunk) : Chunk {
        val nextChunk = Chunk(chunk)
        for (z in 0 until CHUNK_SIZE)
            for (y in 0 until CHUNK_SIZE)
                for (x in 0 until CHUNK_SIZE) {
                    val coordinate = Coordinate(z, y, x)
                    val cube = chunk.getCube(coordinate)
                    val numberOfActiveNeighbors = chunk.getNumberOfActiveNeighbors(coordinate)
                    if (cube == Cube.ACTIVE) {
                        if (numberOfActiveNeighbors == 2 || numberOfActiveNeighbors == 3) {
                            nextChunk.setCube(coordinate, Cube.ACTIVE)
                        }
                    } else {
                        if (numberOfActiveNeighbors == 3) {
                            nextChunk.setCube(coordinate, Cube.ACTIVE)
                        }
                    }
                }
        return nextChunk
    }
}

enum class Cube(var repr: String) {
    ACTIVE("#"),
    INACTIVE("."),
    VOID(" ");

    override fun toString(): String {
        return repr
    }
}

data class Coordinate(val z: Int, val y: Int, val x: Int) {}

class Chunk(
        val chunkX: Int,
        val chunkY: Int
) {
    private var cubes: Array<Array<Array<Cube>>> = Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { Array(CHUNK_SIZE) { Cube.INACTIVE } } }

    constructor(chunk: Chunk) : this(chunk.chunkX, chunk.chunkY)

    fun getCube(relativeCoordinate: Coordinate) : Cube {
        return if (isInChunk(relativeCoordinate))
            cubes[relativeCoordinate.z][relativeCoordinate.y][relativeCoordinate.x]
        else
            Cube.VOID
    }

    fun setCube(relativeCoordinate: Coordinate, cube: Cube) {
        if (isInChunk(relativeCoordinate))
            cubes[relativeCoordinate.z][relativeCoordinate.y][relativeCoordinate.x] = cube
    }

    fun getNeighbors(relativeCoordinate: Coordinate) : List<Cube> {
        val neighbors: ArrayList<Cube> = ArrayList()
        for (z in -1..1) {
            for (y in -1..1) {
                for (x in -1..1) {
                    if (!(z == 0 && y == 0 && x == 0)) {
                        val cube = getCube(Coordinate(relativeCoordinate.z + z,relativeCoordinate.y + y,relativeCoordinate.x + x))
                        if (cube != Cube.VOID) {
                            neighbors.add(cube)
                        }
                    }
                }
            }
        }
        return neighbors
    }
    
    fun getNumberOfActiveNeighbors(relativeCoordinate: Coordinate) : Int {
        return getNeighbors(relativeCoordinate).sumBy { cube -> if (cube == Cube.ACTIVE) 1 else 0 }
    }

    fun getAllCubes() : List<Cube> {
        return cubes.flatten().flatMap { arrayOfCubes -> arrayOfCubes.asList() }
    }

    fun printZ(z: Int) {
        for (y in 0 until CHUNK_SIZE) {
            for (x in 0 until CHUNK_SIZE) {
                print(getCube(Coordinate(z, y, x)))
            }
            print('\n')
        }
    }

    private fun isInChunk(relativeCoordinate: Coordinate) : Boolean {
        return !(relativeCoordinate.z < 0 || relativeCoordinate.y < 0 || relativeCoordinate.x < 0 ||
                relativeCoordinate.z >= CHUNK_SIZE || relativeCoordinate.y >= CHUNK_SIZE || relativeCoordinate.x >= CHUNK_SIZE)
    }
}

fun main() {
    var chunk = Chunk(0, 0)

    val input = File("input.txt").readLines()
    val width = input[0].length
    val height = input.size
    val startCoordinate = Coordinate(CHUNK_SIZE / 2, CHUNK_SIZE / 2 - width / 2, CHUNK_SIZE / 2 - height / 2)
    for (x in 0 until width)
        for (y in 0 until height) {
            val char = input[y][x]
            if (char == '#') {
                val coordinate = Coordinate(startCoordinate.z, startCoordinate.y + y, startCoordinate.x + x)
                chunk.setCube(coordinate, Cube.ACTIVE)
            }
        }

    val world = World()
    for (i in 0 until 6) {
        //chunk.printZ(startCoordinate.z)
        //print('\n')
        chunk = world.nextChunk(chunk)
    }
    chunk.printZ(startCoordinate.z)
    print('\n')

    println(chunk.getAllCubes().filter { cube -> cube == Cube.ACTIVE }.size)
}