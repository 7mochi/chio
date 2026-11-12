{ ... }:
{
  perSystem =
    { pkgs, config, ... }:
    {
      packages.chio = pkgs.callPackage ./chio/package.nix { };
      packages.default = config.packages.chio;
    };
}
