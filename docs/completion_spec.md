# 入力補完 機能仕様

Siren プラグインにおける入力補完機能の詳細な仕様について記載します。

## 1. コード補完 (Code Completion)

IntelliJ IDEA の標準的な補完機能（`Basic Completion`）を通じて、Mermaid のキーワードを提供します。

### 補完対象のキーワード
以下のキーワードが補完候補として表示されます。これらは `MermaidTokenTypes.KEYWORDS_STR` に定義されています。

#### ダイアグラムタイプ
- `C4Context`
- `classDiagram`
- `erDiagram`
- `flowchart`
- `gantt`
- `gitGraph`
- `graph`
- `journey`
- `mindmap`
- `pie`
- `quadrantChart`
- `requirementDiagram`
- `sequenceDiagram`
- `stateDiagram`
- `stateDiagram-v2`
- `timeline`
- `zenuml`
- `sankey-beta`
- `xychart-beta`
- `block-beta`
- `packet`
- `kanban`
- `architecture-beta`
- `radar-beta`
- `treemap-beta`
- `venn-beta`

#### 一般的なキーワード
- `activate`
- `alt`
- `and`
- `as`
- `actor`
- `autonumber`
- `break`
- `callback`
- `class`
- `click`
- `critical`
- `deactivate`
- `else`
- `end`
- `left`
- `link`
- `linkStyle`
- `loop`
- `note`
- `opt`
- `option`
- `over`
- `par`
- `participant`
- `rect`
- `right`
- `style`
- `subgraph`
- `title`

### 動作仕様
- **トリガー**: `Ctrl + Space`（デフォルト）による明示的な呼び出し、または入力中の自動表示。
- **フィルタリング**: 入力された文字に基づいて、候補が前方一致で絞り込まれます。
- **大文字・小文字の区別**: IntelliJ の設定に従いますが、基本的には区別せずに検索し、選択したキーワードがそのまま挿入されます。

## 2. ライブテンプレート (Live Templates)

定型的なダイアグラム構造を、短い略称（Abbreviation）から一気に展開する機能です。

### 提供されるテンプレート

| 略称 (Abbreviation) | 説明 | 展開される内容 |
| :--- | :--- | :--- |
| `flow` | Basic flowchart | `graph TD`<br>`    A[Square Rect] --> B((Circle))`<br>`    A --> C(Round Rect)`<br>`    B --> D{Rhombus}`<br>`    C --> D` |
| `seq` | Basic sequence diagram | `sequenceDiagram`<br>`    participant Alice`<br>`    participant Bob`<br>`    Alice->>Bob: Hello Bob, how are you?`<br>`    Bob-->>Alice: Jolly good!` |
| `class` | Basic class diagram | `classDiagram`<br>`    Class01 <|-- dev.Class02`<br>`    Class03 *-- Class04`<br>`    Class05 o-- Class06` |

### 動作仕様
- **コンテキスト**: Mermaid 言語のファイル（`.mermaid`, `.mmd`）内でのみ有効です。
- **展開方法**: 略称を入力後、`Tab` キー（デフォルト）を押すことで展開されます。

## 3. UI/UX の特徴
- **ルックアップ要素**: 補完候補には、キーワードそのものが表示されます。
- **自動フォーマット**: ライブテンプレートの展開時には、`toReformat="true"` 設定により、プロジェクトのコードスタイル設定に基づいた整形が試みられます。
