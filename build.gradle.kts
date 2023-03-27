@file:Suppress("GradlePackageVersionRange")

import org.apache.commons.io.FileUtils
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val transitiveInclude: Configuration by configurations.creating

plugins {
    id("fabric-loom") version "1.1-SNAPSHOT"
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
    idea
}

base {
    archivesName.set(properties["archives_name"].toString())
    group = property("maven_group")!!
    version = property("mod_version")!!
}

repositories {
    mavenCentral()
    maven("https://repo.repsy.io/mvn/amibeskyfy16/repo")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:${properties["minecraft_version"]}")
    mappings("net.fabricmc:yarn:${properties["yarn_mappings"]}:v2")

    modImplementation("net.fabricmc:fabric-loader:${properties["loader_version"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${properties["fabric_version"]}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${properties["fabric_kotlin_version"]}")
//    modImplementation("net.silkmc:silk-compose:1.0.4")


    transitiveInclude(implementation("ch.skyfy.json5configlib:json5-config-lib:1.0.22")!!)
//    transitiveInclude(implementation("com.github.goxr3plus:FX-BorderlessScene:4.4.0")!!)

    handleIncludes(project, transitiveInclude)

    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

tasks {

    val javaVersion = JavaVersion.VERSION_17

    processResources {
        inputs.property("version", project.version)
        filteringCharset = "UTF-8"
        filesMatching("fabric.mod.json") { expand(mutableMapOf("version" to project.version)) }
    }

    loom {

        runs {
            this.getByName("client") {
                runDir = "testclient"

                val file = File("preconfiguration/doneclient.txt")
                if (!file.exists()) {
                    println("copying to client")
                    file.createNewFile()

                    // Copy some default files to the test client
                    copy {
                        from("preconfiguration/prepared_client/.")
                        into("testclient")
                        include("options.txt") // options.txt with my favorite settings
                    }

                    // Copying the world to use
                    copy {
                        from("preconfiguration/worlds/.")
                        include("testworld#1/**")
                        into("testclient/saves")
                    }

                    // Copying useful mods
                    copy {
                        from("preconfiguration/mods/client/.", "preconfiguration/mods/both/.")
                        include("*.jar")
                        into("testclient/mods")
                    }

                }
            }
            this.getByName("server") {
                runDir = "testserver"

                val file = File("preconfiguration/doneserver.txt")
                if (!file.exists()) {
                    file.createNewFile()
                    println("copying to server")

                    // Copy some default files to the test server
                    copy {
                        from("preconfiguration/prepared_server/.")
                        include("server.properties") // server.properties configured with usefully settings
                        include("eula.txt") // Accepted eula
                        into("testserver")
                    }

                    // Copying the world to use
                    copy {
                        from("preconfiguration/worlds/.")
                        include("testworld#1/**")
                        into("testserver")
                    }

                    // Copying useful mods
                    copy {
                        from("preconfiguration/mods/server/.", "preconfiguration/mods/both/.")
                        include("*.jar")
                        into("testserver/mods")
                    }
                }
            }
        }

    }

    java {
        toolchain {
//            languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
//            vendor.set(JvmVendorSpec.BELLSOFT)
        }
        withSourcesJar()
        withJavadocJar()
    }

    named<Javadoc>("javadoc") {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet")
        }
    }

    named<Wrapper>("wrapper") {
        gradleVersion = "8.0.2"
        distributionType = Wrapper.DistributionType.BIN
    }

    named<KotlinCompile>("compileKotlin") {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }

    named<JavaCompile>("compileJava") {
        options.encoding = "UTF-8"
        options.release.set(javaVersion.toString().toInt())
    }

    named<Jar>("jar") {
        from("LICENSE") {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    named<Test>("test") {
        useJUnitPlatform()

        testLogging {
            outputs.upToDateWhen { false } // When the build task is executed, stderr-stdout of test classes will be show
            showStandardStreams = true
        }
    }

    val copyJarToTestServer = register("copyJarToTestServer") {
        println("copy to server")
//        copyFile("build/libs/${project.properties["archives_name"]}-${project.properties["mod_version"]}.jar", project.property("testServerModsFolder") as String)
//        copyFile("build/libs/${project.properties["archives_name"]}-${project.properties["mod_version"]}.jar", project.property("testClientModsFolder") as String)
    }

    build { doLast { copyJarToTestServer.get() } }

}

fun copyFile(src: String, dest: String) = copy { from(src);into(dest) }

fun DependencyHandlerScope.includeTransitive(
    root: ResolvedDependency?,
    dependencies: Set<ResolvedDependency>,
    fabricLanguageKotlinDependency: ResolvedDependency,
    checkedDependencies: MutableSet<ResolvedDependency> = HashSet()
) {
    dependencies.forEach {
        if (checkedDependencies.contains(it) || (it.moduleGroup == "org.jetbrains.kotlin" && it.moduleName.startsWith("kotlin-stdlib")) || (it.moduleGroup == "org.slf4j" && it.moduleName == "slf4j-api"))
            return@forEach

        if (fabricLanguageKotlinDependency.children.any { kotlinDep -> kotlinDep.name == it.name }) {
            println("Skipping -> ${it.name} (already in fabric-language-kotlin)")
        } else {
            include(it.name)
            println("Including -> ${it.name} from ${root?.name}")
        }
        checkedDependencies += it

        includeTransitive(root ?: it, it.children, fabricLanguageKotlinDependency, checkedDependencies)
    }
}

// from : https://github.com/StckOverflw/TwitchControlsMinecraft/blob/4bf406893544c3edf52371fa6e7a6cc7ae80dc05/build.gradle.kts
fun DependencyHandlerScope.handleIncludes(project: Project, configuration: Configuration) {
    includeTransitive(
        null,
        configuration.resolvedConfiguration.firstLevelModuleDependencies,
        project.configurations.getByName("modImplementation").resolvedConfiguration.firstLevelModuleDependencies
            .first { it.moduleGroup == "net.fabricmc" && it.moduleName == "fabric-language-kotlin" }
    )
}
