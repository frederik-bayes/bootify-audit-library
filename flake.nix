{
  description = "A Nix-flake-based Java development environment";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";

  outputs = { self, nixpkgs }:
    let
      overlay = final: prev: {
        jmc = with final;
          stdenv.mkDerivation rec {
            pname = "jmc";
            version = "8.3.0";

            src = fetchurl {
              url = "https://github.com/adoptium/jmc-build/releases/download/${version}/org.openjdk.jmc-${version}-linux.gtk.x86_64.tar.gz";
              sha256 = "13gzl3nby9n8mbxh20pgybjmhwzpf03v6j8wr095d7ayrkzpshqa";
            };

            nativeBuildInputs = [
              autoPatchelfHook
            ];

            sourceRoot = ".";

            phases = [ "unpackPhase" "installPhase" ];

            installPhase = ''
              runHook preInstall
              cp -R JDK\ Mission\ Control/ $out/
              mkdir -p $out/bin
              ln -s $out/${pname} $out/bin/${pname}
              patchelf --set-interpreter "$(cat $NIX_CC/nix-support/dynamic-linker)" $out/${pname}
              runHook postInstall
            '';

            meta = with lib; {
              homepage = "https://github.com/adoptium/jmc-build";
              description = "Contains the Adoptium specific source code overrides and build pipeline script for the Java Mission Control project.";
              platforms = platforms.linux;
              license = licenses.asl20;
            };
          };
      };
      javaVersion = 21; # Change this value to update the whole stack
      overlays = [
        overlay
        (final: prev: rec {
          jdk = prev."temurin-bin-${toString javaVersion}";
          gradle = prev.gradle.override { java = jdk; };
          maven = prev.maven.override { inherit jdk; };
        })
      ];
      supportedSystems = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
      forEachSupportedSystem = f: nixpkgs.lib.genAttrs supportedSystems (system: f {
        pkgs = import nixpkgs { inherit overlays system; };
      });
    in
    {
      devShells = forEachSupportedSystem ({ pkgs }: {
        default = pkgs.mkShell {
          packages = with pkgs; [ jdk ];
        };
        profiling = pkgs.mkShell {
          packages = with pkgs; [ jdk k6 jmc visualvm ];
        };
      });
    };
}
