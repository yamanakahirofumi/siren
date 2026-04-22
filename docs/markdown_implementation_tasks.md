# Markdownプラグイン内 Mermaid描画機能の実装タスク

標準のMarkdownプラグイン（`org.intellij.plugins.markdown`）のプレビュー画面において、Mermaidダイアグラムをレンダリングするための実装手順を細分化したタスクリストです。

## 完了したタスク

### 1. 環境セットアップ
- [x] `gradle.properties` の `platformBundledPlugins` に `org.intellij.plugins.markdown` を追加する
- [x] `plugin.xml` に `<depends>org.intellij.plugins.markdown</depends>` を追加し、Markdownプラグインへの依存を明示する
- [x] Gradleプロジェクトを再ロードし、Markdownプラグインのライブラリが参照可能であることを確認する

### 2. Kotlinバックエンドの実装
- [x] `MarkdownBrowserPreviewExtension` インターフェースを実装するクラス (`MermaidMarkdownPreviewExtension`) を作成
    - [x] `getScripts()` を実装し、`mermaid.min.js` などのスクリプトパスを返す
    - [x] `getStyles()` を実装（現在は空、必要に応じて追加可能）
    - [x] `getHtml()` を実装（初期化用ロジックはJS側に委譲）
- [x] `MarkdownBrowserPreviewExtensionProvider` インターフェースを実装するクラス (`MermaidMarkdownPreviewExtensionProvider`) を作成
- [x] `ResourceProvider` インターフェースを実装し、プラグインのリソースをJCEFブラウザに提供

### 3. プラグイン設定の更新
- [x] `plugin.xml` に `org.intellij.markdown.browserPreviewExtensionProvider` 拡張ポイントを登録
    ```xml
    <extensions defaultExtensionNs="org.intellij.markdown">
        <browserPreviewExtensionProvider implementation="com.github.yamanakahirofumi.siren.markdown.MermaidMarkdownPreviewExtensionProvider"/>
    </extensions>
    ```

### 4. フロントエンドJavaScriptの実装 (`mermaid_markdown_preview.js`)
- [x] `MutationObserver` を使用して、Markdownプレビュー内の `code.language-mermaid` 要素の動的な出現を監視
- [x] Mermaidの初期化処理
    - [x] `mermaid.initialize()` の呼び出し（ダークモード対応を含む）
    - [x] ダークモードの検知（`body` クラスの `theme-dark` または `prefers-color-scheme` の監視）
- [x] 非同期レンダリング処理
    - [x] `mermaid.render()` を使用して、コードブロックの内容をSVGに変換し、DOMに注入
    - [x] コンテンツセキュリティポリシー (CSP) に配慮し、外部ファイルから読み込む構成
- [x] パフォーマンス対策
    - [x] 頻繁な更新に対するデバウンス（Debounce）処理の実装
- [x] ズーム機能
    - [x] 各ダイアグラムに対するズームコントロールの追加とサイズ調整ロジック

## 今後の課題・検証事項

### 5. テストと継続的な検証
- [ ] 巨大なダイアグラムを含むMarkdownファイルでのパフォーマンスおよびメモリ使用量の検証
- [ ] `VfsRootAccess.allowRootAccess` を使用した、テストデータへのアクセスを含むインテグレーションテストの拡充
- [ ] 新しいMermaid.jsバージョンへの追従とJCEF互換性の確認
- [ ] 特殊なMarkdown構造（リスト内、引用内など）でのレンダリング崩れの確認
