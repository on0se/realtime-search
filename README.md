# realtime-search

Spring Boot の学習用に作った、投稿の検索アプリのバックエンド。

- 投稿の作成・取得・削除とキーワード検索
- 保存先を `PostRepository` で抽象化し、インメモリと Elasticsearch の2実装を用意
- 検索結果の要約を外部のLLM APIに委譲

## 関連リポジトリ

- [realtime_search_fe](https://github.com/on0se/realtime_search_fe) — フロントエンド
- [realtime_search_rag](https://github.com/on0se/realtime_search_rag) — 要約API

## 起動

Elasticsearch と Kibana を立てる。

```bash
docker compose up -d
```

アプリを起動する。

```bash
./gradlew bootRun
```

- アプリ — http://localhost:8080
- Elasticsearch — http://localhost:9200
- Kibana — http://localhost:5601
- 要約API — http://localhost:8000（realtime_search_rag）
