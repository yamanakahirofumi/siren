# Siren 仕様書

## 概要
Siren (Simple Renderer for Mermaid) は、Mermaid ダイアグラムのリアルタイムプレビューを提供する IntelliJ IDEA プラグインです。

## 主な機能
- **リアルタイムプレビュー**: `.mermaid` または `.mmd` ファイルの編集に合わせて、ダイアグラムが自動的に更新されます。
- **ズーム機能**: レンダリングされたダイアグラムの拡大・縮小に対応しています。
- **統合エディタ**: IntelliJ IDEA のエディタとシームレスに統合されており、通常のテキストエディタの隣にプレビューが表示されます。
- **軽量設計**: レンダリング性能に重点を置いたミニマリストな設計です。

## サポートされるファイル形式
- `.mermaid`
- `.mmd`

## 技術アーキテクチャ

### コンポーネント構成
1. **MermaidFileType / MermaidLanguage**: IntelliJ における Mermaid ファイルの種類と言語サポートを定義します。
2. **MermaidEditorProvider**: Mermaid ファイルを検出し、`MermaidPreviewEditor` を提供します。
3. **MermaidPreviewEditor**: ダイアグラムを表示するための JCEF ブラウザをホストするメイン UI コンポーネントです。ドキュメントの変更を監視し、更新をトリガーします。
4. **MermaidPreviewServer**: HTML テンプレートと Mermaid コンテンツを提供する組み込み HTTP サーバ (`com.sun.net.httpserver.HttpServer` を使用) です。
5. **リソース**:
   - `preview.html`: レンダリングに使用される HTML テンプレート。
   - `mermaid.min.js`: Mermaid.js ライブラリ (v11.8.1)。

### 動作フロー
1. Mermaid ファイルが開かれると、`MermaidEditorProvider` が `MermaidPreviewEditor` を生成します。
2. `MermaidPreviewEditor` は、利用可能なポートで `MermaidPreviewServer` を起動します。
3. ユーザがエディタで入力をすると、`DocumentListener` が変更を検知します。
4. エディタは更新されたダイアグラムのテキストを `MermaidPreviewServer` に送信します。
5. `MermaidPreviewServer` は内部状態を更新し、JCEF ブラウザがプレビュー URL をリロードします。
6. ブラウザ内蔵の `preview.html` で `mermaid.render()` が実行され、ダイアグラムが描画されます。

## 依存関係
- IntelliJ Platform SDK
- Mermaid.js v11.8.1
- JCEF (Java Chromium Embedded Framework): レンダリング用
