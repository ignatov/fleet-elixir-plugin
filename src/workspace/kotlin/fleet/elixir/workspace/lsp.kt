package fleet.elixir.workspace

import com.jetbrains.rhizomedb.Entity
import com.jetbrains.rhizomedb.Entrypoint
import fleet.api.HostId
import fleet.api.ProjectRoot
import fleet.api.ProtocolPath
import fleet.api.workspace.document.MediaType
import fleet.elixir.common.elixirLspPathSettingKey
import fleet.common.ExecService
import fleet.common.fs.Pattern
import fleet.common.settings.querySettingKeyOnHost
import fleet.kernel.ChangeScope
import fleet.kernel.Kernel
import fleet.util.logging.KLoggers
import fleet.workspace.lsp.*

private val logger by lazy { KLoggers.logger(ElixirLspConfiguration::class) }

@Entrypoint
fun ChangeScope.init() {
    new(ElixirLspController::class) {
        restartNeeded = false
    }
}

interface ElixirLspController : LspControllerEntity {
    override val projectFilePatterns: List<Pattern> get() = listOf(Pattern("mix.exs")) // using the pattern Fleet will start elixir-lsp
    override val label: String get() = "elixir" // label to mark a corresponding LSP in UI, should be unique
    override val lspConfiguration: LspConfiguration get() = ElixirLspConfiguration()
}

class ElixirLspConfiguration : LspConfiguration {
    override val identifier = "elixir-language-server"
    override val mediaTypes = listOf( MediaType("text", "elixir"), MediaType("text", "exs")) // supported file types

    override fun createLanguageClient(lifetime: Entity, kernel: Kernel, hostId: HostId): LspLanguageClient {
        return LspLanguageClient(lifetime, kernel, this, hostId)
    }

    override suspend fun findExecutable(
        exec: ExecService,
        projectRoots: List<ProjectRoot>,
        progressReporter: LspProgressReporter
    ): LspExecutable {
        val configuredPath = exec.querySettingKeyOnHost(elixirLspPathSettingKey)
        val lspServerPath = if (configuredPath.isNotEmpty()) {
            logger.debug { "Using custom elixir server path: $configuredPath" }
            configuredPath
        } else {
            null
        }

        if (lspServerPath == null) {
            error("Can't find elixir lsp server, specify it with \"elixir.lsp.path\" in ~/.fleet/settings.json")
        }

        return LspExecutable(ProtocolPath.of(lspServerPath), emptyList())
    }
}
