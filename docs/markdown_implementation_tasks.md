# Markdownプラグイン内 Mermaid描画機能の実装タスク

標準のMarkdownプラグイン（`org.intellij.plugins.markdown`）のプレビュー画面において、Mermaidダイアグラムをレンダリングするための実装手順を細分化したタスクリストです。

## 1. 環境セットアップ
- [x] `gradle.properties` の `platformBundledPlugins` に `org.intellij.plugins.markdown` を追加する
- [x] `plugin.xml` に `<depends>org.intellij.plugins.markdown</depends>` を追加し、Markdownプラグインへの依存を明示する
- [x] Gradleプロジェクトを再ロードし、Markdownプラグインのライブラリが参照可能であることを確認する

## 2. Kotlinバックエンドの実装
- [x] `MarkdownBrowserPreviewExtension` インターフェースを実装するクラス（例: `MermaidMarkdownPreviewExtension`）を作成する
    - [x] `getScripts()` を実装し、`mermaid.min.js` などのスクリプトパスを返すようにする
    - [x] `getStyles()` を実装し、必要に応じてプレビュー用のCSSを定義する
    - [x] `getHtml()` を実装し、Mermaidの描画コンテナや初期化用の隠し要素などを定義する
- [x] `MarkdownBrowserPreviewExtensionProvider` インターフェースを実装するクラス（例: `MermaidMarkdownPreviewExtensionProvider`）を作成する
- [x] `ResourceProvider` インターフェースを実装し、プラグインのリソース（`mermaid.min.js`など）をJCEFブラウザに提供する仕組みを構築する

## 3. プラグイン設定の更新
- [x] `plugin.xml` に `org.intellij.markdown.browserPreviewExtensionProvider` 拡張ポイントを登録する
    ```xml
    <extensions defaultExtensionNs="org.intellij.markdown">
        <browserPreviewExtensionProvider implementation="com.github.yamanakahirofumi.siren.markdown.MermaidMarkdownPreviewExtensionProvider"/>
    </extensions>
    ```

## 4. フロントエンドJavaScriptの実装
- [x] `MutationObserver` を使用して、Markdownプレビュー内の `code.language-mermaid` 要素の動的な出現を監視するスクリプトを作成する
- [x] Mermaidの初期化処理を実装する
    - [x] `mermaid.initialize()` の呼び出し（ダークモード対応を含む）
    - [x] ダークモードの検知（`body` クラスの `theme-dark` または `prefers-color-scheme` の監視）
- [x] 非同期レンダリング処理の実装
    - [x] `mermaid.run()` または `mermaid.render()` を使用して、コードブロックの内容をSVGに変換し、DOMに注入する
    - [x] コンテンツセキュリティポリシー (CSP) に配慮し、インラインスクリプトを避け、外部ファイルから読み込むように構成する
- [x] パフォーマンス対策
    - [x] 頻繁な更新に対するデバウンス（Debounce）処理の検討

## 5. テストと検証
- [ ] Markdownファイル内のMermaidブロックが正しくレンダリングされることを確認する
- [ ] ダークモード/ライトモードの切り替え時に色が正しく追従することを確認する
- [ ] ズーム操作やウィンドウのリサイズ時にダイアグラムが崩れないことを確認する
- [ ] 巨大なダイアグラムを含むMarkdownファイルでパフォーマンス上の問題がないか検証する
- [ ] `VfsRootAccess.allowRootAccess` を使用した、テストデータへのアクセスを含むインテグレーションテストを作成する
