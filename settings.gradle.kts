pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KotlinAngroidGraphicsProject7"
include(":app")
include(":leftlineperprotview")
include(":linehalfshiftdownview")
include(":biarcbentupview")
include(":bialtarcsweepview")
include(":concrotsiderightview")
include(":parallellinejoindownview")
include(":linebentarcrotview")
include(":sidelinearcdownview")
include(":linerotpartialarcview")
include(":biquartersemiarcview")
include(":mirrorintosemicircleview")
include(":lineleftquarterencloseview")
include(":bitriangleencloseview")
include(":lineshiftarcupview")
include(":lineextendarcrotview")
include(":arcrotateleftlineview")
include(":lineextendarcdownview")
include(":arcsqextendownview")
include(":arcjoinlinedownview")
include(":linebisectarcjoinview")
include(":breakarcjoinrightview")
include(":breaklinearcdownview")
include(":parallellinesemicircleview")
include(":sweeplinearcupview")
include(":rotarccreatelineview")
include(":partialrotarcrightview")
include(":linejoinendarcview")
include(":parallellinterlinejoinview")
include(":arclineslantrightview")
include(":bentlinearcrightview")
include(":joinlinesemicircleview")
include(":linearcdroprotview")
include(":linesemiarcfullview")
include(":linebentarcfullview")
