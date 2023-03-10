module fleet.elixir.workspace {
  requires fleet.elixir.common;
  requires kotlin.stdlib;
  requires fleet.util.logging.api;
  requires fleet.kernel;
  requires fleet.lsp;
  requires fleet.common;
  requires fleet.workspace;

  exports fleet.elixir.workspace;
}