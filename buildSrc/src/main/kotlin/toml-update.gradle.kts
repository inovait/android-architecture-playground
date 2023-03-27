import org.gradle.accessors.dm.LibrariesForLibs
import org.json.JSONObject
import org.tomlj.Toml
import org.tomlj.TomlTable

val libs = the<LibrariesForLibs>()

afterEvaluate {
   tasks.register<UpdateTomlLibs>("updateLibsToml") {
      reportFiles = project.fileTree(project.rootDir).apply {
         include("**/build/dependencyUpdates/versions.json")
      }

      tomlFile = File(project.rootDir, "config/libs.toml")
   }
}

open class UpdateTomlLibs : DefaultTask() {
   @InputFiles
   lateinit var reportFiles: FileTree

   @InputFile
   lateinit var tomlFile: File

   @TaskAction
   fun execute() {
      val requestedUpdates = parseDependencyUpdates()

      val originalTomlText = tomlFile.readText()

      val libsToml = Toml.parse(originalTomlText)
      if (libsToml.hasErrors()) {
         for (error in libsToml.errors()) {
            error.printStackTrace()
         }

         error("TOML parsing of the $tomlFile failed")
      }

      val libraries = requireNotNull(libsToml.getTable("libraries")) {
         "$tomlFile does not contain libraries section"
      }

      val targetBumps = HashMap<String, String>()

      for (entry in libraries.entrySet()) {
         val tomlValue = entry.value as TomlTable
         val module = tomlValue.get("module")
         val versionRef = tomlValue.get("version.ref") as String
         val targetVersion = requestedUpdates[module]

         if (targetVersion != null) {
            targetBumps[versionRef] = targetVersion
         }
      }

      if (targetBumps.isEmpty()) {
         println("Nothing to update")
         return
      }

      var updatedText = originalTomlText
      for (bump in targetBumps) {
         val currentVersion = libsToml.get("versions.${bump.key}") as String
         println("Bumping ${bump.key} from $currentVersion to ${bump.value}")

         updatedText = updatedText.replace("${bump.key} = \"$currentVersion\"", "${bump.key} = \"${bump.value}\"")
      }

      tomlFile.writeText(updatedText)
   }

   private fun parseDependencyUpdates(): Map<String, String> {
      val requestedUpdates = HashMap<String, String>()

      for (dependencyReport in reportFiles) {
         val jsonString = dependencyReport.readText()
         val obj: JSONObject = JSONObject(jsonString)

         obj.getJSONObject("outdated").getJSONArray("dependencies").forEach {
            val dependencyObj = it as JSONObject

            val group = dependencyObj.getString("group")
            val name = dependencyObj.getString("name")

            val available = dependencyObj.getJSONObject("available")

            val newVersion = available.getStringOrNull("release")
               ?: available.getStringOrNull("milestone")
               ?: available.getStringOrNull("integration")
               ?: return@forEach

            requestedUpdates["$group:$name"] = newVersion
         }
      }
      return requestedUpdates
   }

   private fun JSONObject.getStringOrNull(key: String): String? {
      return if (isNull(key)) {
         null
      } else {
         getString(key)
      }
   }
}
