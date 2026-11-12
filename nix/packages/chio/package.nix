{
  lib,
  stdenv,
  pkgs,
  jdk25,
}:
let
  gradle =
    (pkgs.gradle-packages.mkGradle {
      version = "9.9.1";
      hash = "sha256-2KGKeoQd1d+CXTWmhfrW8tfxKsDCgOV7Z8J7SlILoig=";
      defaultJava = jdk25;
      updateScriptMajorVersion = "9";
    }).wrapped;
in
stdenv.mkDerivation (finalAttrs: {
  pname = "chio";
  version = "1.0-SNAPSHOT";

  src = lib.fileset.toSource {
    root = ../../..;
    fileset = lib.fileset.unions [
      ../../../.gitignore
      ../../../README.md
      ../../../build.gradle.kts
      ../../../settings.gradle.kts
      ../../../gradle.properties
      ../../../src
    ];
  };

  nativeBuildInputs = [
    gradle
    jdk25
  ];

  mitmCache = gradle.fetchDeps {
    pkg = finalAttrs.finalPackage;
    data = ./deps.json;
  };

  gradleBuildTask = "build";
  gradleUpdateTask = "nixDownloadDeps spotlessCheck";

  # nixpkgs' dependency task still uses the Gradle 8 task API.
  # Re-run the configure hook with a Gradle 9 compatible init script.
  gradleUpdateScript = ''
    runHook preBuild
    runHook preGradleUpdate
    gradleFlagsArray=()
    gradleInitScript=${./init-deps.gradle}
    gradleConfigureHook
    gradle --no-configuration-cache ''${gradleUpdateTask:-nixDownloadDeps}
    runHook postGradleUpdate
  '';

  installPhase = ''
    runHook preInstall

    mkdir -p $out/share/java
    cp build/libs/chio-*.jar $out/share/java/

    runHook postInstall
  '';

  meta = {
    description = "Kotlin library for osu! Bancho packet serialization";
  };
})
