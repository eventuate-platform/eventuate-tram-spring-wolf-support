package io.eventuate.tram.springwolf.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.*

abstract class ValidateAsyncAPITask extends Exec {
    ValidateAsyncAPITask() {
        description = 'Validates the AsyncAPI specification using asyncapi validate'
        group = 'verification'
    }

    @InputFile
    @PathSensitive(PathSensitivity.NONE)
    File getSpecificationFile() {
        return project.file('build/springwolf.json')
    }

    @OutputFile
    File getOutputFile() {
        return project.file('build/springwolf.json.validated')
    }

    @Override
    protected void exec() {
        if (!getSpecificationFile().exists()) {
            throw new IllegalStateException("springwolf.json file not found in ${getSpecificationFile()}")
        }
        workingDir = project.projectDir
        commandLine 'npx', '@asyncapi/cli', 'validate', getSpecificationFile().absolutePath
        super.exec()
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
