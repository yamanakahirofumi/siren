# 入力補助機能

Siren は、Mermaid ダイアグラムを効率的に作成するための様々な入力補助機能を提供しています。

## 1. 構文ハイライト (Syntax Highlighting)

Mermaid の構文を色分けして表示し、コードの可読性を高めます。以下の要素がハイライトされます：

- **キーワード**: `graph`, `flowchart`, `sequenceDiagram`, `classDiagram` などのダイアグラム定義や、`subgraph`, `participant`, `loop` などの制御構文。
- **コメント**: `%%` で始まる行。
- **括弧 (Braces)**: `()`, `[]`, `{}`。
- **矢印 (Arrows)**: `-->`, `->>`, `--x` などのリレーション。
- **テキスト**: 識別子やラベルなどの一般的なテキスト。

## 2. コード補完 (Code Completion)

Mermaid の主要なキーワードを補完します。
ダイアグラムの種類や、その中で使用される特殊なキーワードを素早く入力できます。
詳細な仕様については、[入力補完 機能仕様](completion_spec.md) を参照してください。

## 3. ライブテンプレート (Live Templates)

よく使われるダイアグラムの雛形を短いキーワードで挿入できます。以下のテンプレートが利用可能です。
詳細な仕様については、[入力補完 機能仕様](completion_spec.md) を参照してください。

| 略称 | 説明 | 挿入される内容 (抜粋) |
| :--- | :--- | :--- |
| `flow` | 基本的なフローチャート | `graph TD` ... |
| `seq` | 基本的なシーケンス図 | `sequenceDiagram` ... |
| `class` | 基本的なクラス図 | `classDiagram` ... |

### サポートされるダイアグラムタイプと書き方パターン

Siren は以下の Mermaid ダイアグラムタイプをサポートし、それぞれの書き方パターンに基づいた入力補助を提供します。

| ダイアグラムタイプ | キーワード | 書き方パターン (例) |
| :--- | :--- | :--- |
| フローチャート | `flowchart` / `graph` | `flowchart TD` <br> `A --> B` |
| シーケンス図 | `sequenceDiagram` | `sequenceDiagram` <br> `Alice ->> Bob: Hello` |
| クラス図 | `classDiagram` | `classDiagram` <br> `Class01 <\|-- Class02` |
| 状態遷移図 | `stateDiagram-v2` / `stateDiagram` | `stateDiagram-v2` <br> `[*] --> State1` |
| 実体関連図 (ER図) | `erDiagram` | `erDiagram` <br> `ENTITY { string name }` |
| ユーザージャーニー図 | `journey` | `journey` <br> `section My journey` <br> `Task: 5: Me` |
| ガントチャート | `gantt` | `gantt` <br> `section Section` <br> `Task :a1, 2024-01-01, 30d` |
| 円グラフ | `pie` | `pie title Pet` <br> `"Dogs" : 386` |
| クアドラントチャート | `quadrantChart` | `quadrantChart` <br> `x-axis Low --> High` |
| 要求図 | `requirementDiagram` | `requirementDiagram` <br> `requirement r1 { text: "req" }` |
| Gitグラフ | `gitGraph` | `gitGraph` <br> `commit` <br> `branch dev` |
| C4図 | `C4Context` | `C4Context` <br> `Person(p1, "Name")` |
| マインドマップ | `mindmap` | `mindmap` <br> `root` <br> `  child` |
| タイムライン | `timeline` | `timeline` <br> `2024 : Event` |
| ZenUML | `zenuml` | `zenuml` <br> `Alice.method()` |
| サンキーダイアグラム | `sankey-beta` | `sankey-beta` <br> `A,B,10` |
| XYチャート | `xychart-beta` | `xychart-beta` <br> `title "XY"` <br> `x-axis [1, 2]` |
| ブロック図 | `block-beta` | `block-beta` <br> `columns 1` <br> `A` |
| パケット図 | `packet` | `packet` <br> `0-15: "Header"` |
| かんばん図 | `kanban` | `kanban` <br> `Todo[Todo]` <br> `  task1[Task 1]` |
| アーキテクチャ図 | `architecture-beta` | `architecture-beta` <br> `service s1(server)[S1]` |
| レーダーチャート | `radar-beta` | `radar-beta` <br> `axis A` <br> `curve c1{1}` |
| ツリーマップ | `treemap-beta` | `treemap-beta` <br> `"Root"` <br> `  "Child": 10` |
| ベン図 | `venn-beta` | `venn-beta` <br> `set A` <br> `set B` <br> `union A,B` |

## 4. コメント機能 (Commenting)

エディタの標準ショートカット（通常は `Ctrl + /` または `Cmd + /`）を使用して、選択した行を `%%` でコメントアウトまたは解除できます。

## 5. 括弧のペアリング (Brace Matching)

`()`, `[]`, `{}` などの括弧を入力した際、対応する閉じ括弧を自動的に補完したり、片方の括弧にカーソルを置いたときに対応するもう一方の括弧を強調表示したりします。
