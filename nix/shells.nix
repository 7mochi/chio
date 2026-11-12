{ ... }:
{
  perSystem =
    { pkgs, ... }:
    {
      devShells.default = pkgs.mkShell {
        packages = with pkgs; [
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
        '';
      };
    };
}
