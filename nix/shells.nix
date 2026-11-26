{ ... }:
{
  perSystem =
    { pkgs, ... }:
    {
      devShells.default = pkgs.mkShell {
        packages = with pkgs; [
          bump-my-version
          jdk25
          just
          nixfmt
        ];

        shellHook = ''
          echo "chio"
          echo "  just build"
          echo "  just check"
          echo "  just test"
          echo "  just format"
          echo "  just lint"
          echo "  just clean"
          echo "  just bump [patch|minor|major]"
        '';
      };
    };
}
