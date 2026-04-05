# gradle.properties の各パラメータの意味

このプロジェクトの `gradle.properties` で定義されている各パラメータの役割と意味について説明します。

## プラグイン基本情報

- **pluginGroup**: プラグインのグループID（パッケージ名に相当）。通常、GitHubのユーザー名や組織名を含むリバースドメイン形式で指定します。
- **pluginName**: プラグインの名称。IntelliJ IDEA内のプラグインマネージャーなどで表示される名前です。
- **pluginRepositoryUrl**: プロジェクトのソースコードがホストされているURL。
- **pluginVersion**: プラグインの現在のバージョン。SemVer（セマンティック・バージョニング）形式で記述します。

## IntelliJ Platform 設定

- **pluginSinceBuild**: プラグインがサポートを開始する最小のビルド番号。例えば `261` は IntelliJ Platform 2026.1 以降を意味します。
- **platformVersion**: 開発およびビルドに使用する IntelliJ Platform のバージョン（例: `2026.1`）。

## 依存関係管理

- **platformPlugins**: JetBrains Marketplace から導入する外部プラグインの依存関係をカンマ区切りで指定します。
- **platformBundledPlugins**: IntelliJ Platform に標準で同梱されているプラグインへの依存関係を指定します。このプロジェクトでは `org.intellij.plugins.markdown`（Markdownプラグイン）が指定されています。
- **platformBundledModules**: IntelliJ Platform の特定のモジュールへの依存関係を指定します。

## Gradle および Kotlin 設定

- **gradleVersion**: プロジェクトで使用する Gradle のバージョン。
- **kotlin.stdlib.default.dependency**: Kotlin 標準ライブラリをデフォルトで依存関係に含めるかどうか。`false` に設定されている場合、明示的な管理が必要ですが、IntelliJ Platform に含まれるライブラリとの競合を避けるために使われることがあります。

## Gradle パフォーマンス設定

- **org.gradle.configuration-cache**: Gradle の構成キャッシュを有効にするかどうか。ビルド時間の短縮に寄与します。
- **org.gradle.caching**: Gradle のビルドキャッシュを有効にするかどうか。過去のビルド結果を再利用することでビルドを高速化します。
