/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gradle.plugins

import com.google.gradle.util.android
import com.google.gradle.util.release
import com.google.gradle.util.tempFile
import java.io.File
import kotlinx.validation.KotlinApiBuildTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * A Gradle plugin for creating `.api` files; representing the public API of the project.
 *
 * By default, a `public.api` file (at the root of the project) will be used as a base for the
 * released api.
 *
 * Registers two tasks:
 * - `buildApi` -> creates a `.api` file containing the *current* public API of the project.
 * - `updateApi` -> updates the `public.api` file at the project root to match the one generated by
 *   `buildApi`; effectively saying that the released api is up to date with the current repo state.
 *
 * @see ApiPluginExtension
 * @see ChangelogPluginExtension
 */
abstract class ApiPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val extension = extensions.create<ApiPluginExtension>("api").apply { commonConfiguration() }

      val buildApi = registerBuildApiTask()

      tasks.register<Copy>("updateApi") {
        val fileName = extension.apiFile.map { it.name }
        val filePath = extension.apiFile.map { it.parent }

        from(buildApi)
        into(filePath)

        rename { fileName.get() }
      }
    }
  }

  private fun Project.registerBuildApiTask() =
    tasks.register<KotlinApiBuildTask>("buildApi") {
      val classes = provider { android.release.output.classesDirs }

      inputClassesDirs = files(classes)
      inputDependencies = files(classes)
      outputApiDir = tempFile("api").get()
    }

  context(Project)
  private fun ApiPluginExtension.commonConfiguration() {
    apiFile.convention(file("public.api"))
  }
}

/**
 * Extension properties for the [ApiPlugin].
 *
 * @property apiFile The file to reference as (and save to) in regards to the publicly released api.
 */
abstract class ApiPluginExtension {
  @get:Optional abstract val apiFile: Property<File>
}
