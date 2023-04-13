import java.nio.file.Paths
import kotlin.test.Test

class Tests {

    /**
     * Just a code to sort some folders by date
     */
    @Test
    fun sortFolders(){
        val names1 = listOf(
            "dev",
            "tutorials",
            "resources",
            "tools",
            "release"
        )

        val names2 = listOf(
            "client",
            "server",
            "common"
        )


        val root = Paths.get("E:\\Tech\\Projects\\MC\\MTEA_2023\\0.0.1")

        var now = System.currentTimeMillis()
        names1.forEach {
            root.resolve(it).toFile().setLastModified(now)
            now += 60_000
        }

        names2.forEach {
            root.resolve(it).toFile().setLastModified(now)
            now += 60_000
        }

    }


}