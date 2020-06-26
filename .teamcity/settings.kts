import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2020.1"

project {

    vcsRoot(HttpsGithubComMovshukPhonebookGitRefsHeadsMaster)

    buildType(Build)
}

object Build : BuildType({
    name = "Build"

    artifactRules = "target/phonebook-0.0.1-SNAPSHOT.jar"

    vcs {
        root(HttpsGithubComMovshukPhonebookGitRefsHeadsMaster)
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            name = "MavenStep"
            enabled = false
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true -DskipTests"
        }
        script {
            name = "DockerCommandStep"
            scriptContent = "docker-compose up --build"
        }
    }

    triggers {
        vcs {
        }
    }

    dependencies {
        artifacts(RelativeId("Build")) {
            buildRule = lastSuccessful()
            artifactRules = "phonebook-0.0.1-SNAPSHOT.jar"
        }
    }
})

object HttpsGithubComMovshukPhonebookGitRefsHeadsMaster : GitVcsRoot({
    name = "PhoneBook"
    url = "https://github.com/Movshuk/phonebook.git"
    authMethod = password {
        userName = "pochta.mvn@mail.ru"
        password = "credentialsJSON:dcccaac6-9a99-4559-9489-d8edfc53e726"
    }
})
