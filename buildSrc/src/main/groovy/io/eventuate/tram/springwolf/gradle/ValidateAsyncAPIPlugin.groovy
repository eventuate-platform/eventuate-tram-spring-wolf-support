package io.eventuate.tram.springwolf.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.gradle.api.file.FileCollection

abstract class ValidateAsyncAPITask extends Exec {
    ValidateAsyncAPITask() {
        description = 'Validates the AsyncAPI specification using asyncapi validate'
        group = 'verification'
    }

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    FileCollection getSpecificationFiles() {
        return project.fileTree(dir: 'build', include: 'springwolf*.json')
    }

    @OutputFile
    File getOutputFile() {
        return project.file('build/springwolf.json.validated')
    }

    @Override
    protected void exec() {
        def files = getSpecificationFiles()
        if (files.empty) {
            throw new IllegalStateException("No springwolf*.json files found in build directory")
        }

        workingDir = project.projectDir
        def baseCommand = ['npx', '@asyncapi/cli', 'validate']

        files.each { file ->
            logger.info("Validating AsyncAPI specification: ${file.absolutePath}")
            commandLine = baseCommand + [file.absolutePath]
            super.exec()
        }

        getOutputFile().text = System.currentTimeMillis()
    }
}

class ValidateAsyncAPIPlugin implements Plugin<Project> {
    void apply(Project project) {
        def validateTask = project.tasks.register('validateAsyncAPI', ValidateAsyncAPITask)

        project.afterEvaluate {
            project.tasks.named('test').configure {
                finalizedBy(validateTask)
            }
        }
    }
}
