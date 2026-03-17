# MarkdownプラグインにおけるMermaid描画の拡張仕様

IntelliJ IDEAの標準Markdownプラグイン（`org.intellij.plugins.markdown`）において、Mermaidダイアグラムの描画を別のプラグイン（Sirenなど）で実施・拡張するための実装方法を記載します。

## 1. 依存関係の設定

Markdownプラグインの拡張機能を利用するためには、まず対象のプラグインへの依存を定義する必要があります。

### `gradle.properties`
Markdownプラグインをバンドルされたプラグインとして追加します。

```properties
platformBundledPlugins = org.intellij.plugins.markdown
```

### `plugin.xml`
プラグインの定義ファイルに依存関係を明記します。

```xml
<depends>org.intellij.plugins.markdown</depends>
```

## 2. 拡張ポイントの実装

Markdownプラグインのプレビュー画面（JCEFブラウザ）にスクリプトやスタイルを注入するために、以下のクラスを実装します。

### 2.1. `MarkdownBrowserPreviewExtension`
ブラウザに注入するリソース（JS, CSS）やHTML構造を定義するインターフェースです。

- `getScripts()`: Mermaid.jsなどのスクリプトパスを返します。
- `getStyles()`: CSSパスを返します。
- `getHtml()`: レンダリングに必要なHTMLパーツを返します。

### 2.2. `MarkdownBrowserPreviewExtensionProvider`
上記Extensionのインスタンスを提供するクラスです。

## 3. 拡張ポイントの登録

`plugin.xml` に拡張ポイントを登録します。

```xml
<extensions defaultExtensionNs="org.intellij.markdown">
    <browserPreviewExtensionProvider implementation="YOUR_IMPLEMENTATION_CLASS_PATH"/>
</extensions>
```

これにより、Markdownファイルのプレビュー時に自作のMermaidレンダリングロジックが組み込まれるようになります。
