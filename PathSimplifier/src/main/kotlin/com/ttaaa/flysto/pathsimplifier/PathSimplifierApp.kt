package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.simplifier.DouglasPeuckerSimplifier

@Command(
    name = "myapp",
    mixinStandardHelpOptions = true,
    description = ["A Kotlin CLI app using either -f or -g"],
)
class MyApp : Callable<Int> {

    @Option(
        names = ["-f"],
        description = ["Path to file"],
        required = false
    )
    var filePath: String? = null

    @Option(
        names = ["-g"],
        arity = "1..2", // allow 1 or 2 integers
        description = ["One or two integer values"],
        required = false
    )
    var gValues: List<Int>? = null

    override fun call(): Int {
        if ((filePath == null && gValues == null) || (filePath != null && gValues != null)) {
            System.err.println("Error: Specify either -f OR -g (not both)")
            return 1
        }

        if (filePath != null) {
            println("File path: $filePath")
        } else if (gValues != null) {
            println("G values: ${gValues!!.joinToString()}")
        }

        return 0
    }
}

fun main(args: Array<String>) {
    val exitCode = CommandLine(MyApp()).execute(*args)
    System.exit(exitCode)
}
