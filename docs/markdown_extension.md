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

## 4. JavaScript使用時の注意事項・制限事項

JCEFブラウザ内でのJavaScript実行には、いくつかの重要な注意事項と制限事項があります。

### 4.1. コンテンツセキュリティポリシー (CSP)
Markdownプラグインのプレビュー画面には、セキュリティ上の理由から厳格なCSPが設定されています。
- **インラインスクリプトの制限**: `onclick` 属性や `<script>` タグ内の直接的なコード記述は制限される場合があります。可能な限り `getScripts()` を通じて外部JSファイルを読み込むようにしてください。
- **外部リソースへのアクセス**: 外部ドメインからのスクリプト読み込みやAPIリクエストはデフォルトでブロックされます。必要なライブラリはプラグイン内に同梱（bundled）する必要があります。

### 4.2. 実行タイミングと非同期処理
- **DOM Ready**: スクリプトが実行される時点でDOMが完全に構築されているとは限りません。Mermaidの初期化などは `DOMContentLoaded` イベントを待つか、Markdownプラグインが提供するライフサイクルイベントに同期させる必要があります。
- **レンダリングの遅延**: 巨大なダイアグラムのレンダリングは、JCEFのメインスレッドをブロックし、IDEの操作感に影響を与える可能性があります。特に、ドキュメントの更新頻度が高い場合は、デバウンス（Debounce）処理を入れるなどの対策を検討してください。

### 4.3. JCEFの互換性
- **ブラウザ機能の制限**: JCEFはChromiumベースですが、標準のブラウザで利用可能なすべての機能（WebAssemblyの特定の機能や最新のWeb APIなど）が常に利用可能であるとは限りません。Mermaidのバージョンを上げる際は、JCEF上での動作確認を十分に行ってください。
- **ズームとリサイズ**: JCEF内でのズーム操作やウィンドウのリサイズ時に、MermaidのSVGが正しく再描画されないことがあります。リサイズイベントを監視して `mermaid.run()` などを再実行する処理が必要になる場合があります。

### 4.4. ログ出力とデバッグ
- `console.log()` の出力は、IntelliJの標準ログ（`idea.log`）には出力されません。デバッグの際は、JCEFのデベロッパーツール（内部的にポートを開いてブラウザからアクセスする設定が必要）を利用するか、Java側へのコールバック関数を介してログを渡す仕組みを構築する必要があります。

## 5. デバッグ方法 (JCEF DevTools)

Markdown プレビューや Siren 独自のプレビューで発生する JavaScript のエラーを調査するために、JCEF のデベロッパーツールを使用できます。

1. **レジストリの設定**:
   - `Shift` キーを 2 回押し、「Registry...」と入力して選択します。
   - `ide.browser.jcef.debug.port` を探し、空いているポート番号（例: `9222`）を設定します。
2. **IDE の再起動**: 設定を反映させるために IDE を再起動します。
3. **ブラウザからアクセス**:
   - プレビューを表示した状態で、Chrome 等のブラウザから `http://localhost:9222` にアクセスします。
   - 表示されているプレビューのリンクを選択すると、おなじみのデベロッパーツールが開きます。
