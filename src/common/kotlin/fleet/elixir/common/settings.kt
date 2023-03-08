package fleet.elixir.common

import com.jetbrains.rhizomedb.Entrypoint
import fleet.common.settings.SettingsEP
import fleet.common.settings.SettingsKey
import fleet.common.settings.SettingsLocation
import fleet.kernel.ChangeScope
import fleet.kernel.register

val elixirLspPathSettingKey = SettingsKey(
    key = "elixir.lsp.path",
    defaultValue = "",
    supportContexts = false,
    presentableName = "Path of the Elixir language-server executable",
    locations = setOf(SettingsLocation.HOST)
)

@Entrypoint
fun ChangeScope.settings() {
    register {
        SettingsEP.register(elixirLspPathSettingKey.key) { elixirLspPathSettingKey }
    }
}