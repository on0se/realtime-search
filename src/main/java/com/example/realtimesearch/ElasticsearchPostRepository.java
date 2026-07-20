package com.example.realtimesearch;

import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicLong;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;

@Repository
@Primary
public class ElasticsearchPostRepository implements PostRepository {

    private final ElasticsearchOperations operations;
    private final AtomicLong idCounter = new AtomicLong(1);

    public ElasticsearchPostRepository(ElasticsearchOperations operations) {
        this.operations = operations;
    }

    @Override
    public Post save(Post post) {
        if (post.getId() == null) {
            post.setId(idCounter.getAndIncrement());
        }
        PostDocument document = toDocument(post);
        PostDocument saved = operations.save(document);
        return toPost(saved);
    }

    @Override
    public List<Post> findAll() {
        Query query = Query.findAll();
        SearchHits<PostDocument> hits = operations.search(query, PostDocument.class);
        return hits.stream()
                .map(hit -> toPost(hit.getContent()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Post> findById(Long id) {
        PostDocument document = operations.get(String.valueOf(id), PostDocument.class);
        return Optional.ofNullable(document).map(this::toPost);
    }

    @Override
    public void deleteById(Long id) {
        operations.delete(String.valueOf(id), PostDocument.class);
    }

    @Override
    public List<Post> search(String keyword) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.match(MatchQuery.of(m -> m.field("content").query(keyword))))
                .build();
        SearchHits<PostDocument> hits = operations.search(query, PostDocument.class);
        return hits.stream()
                .map(hit -> toPost(hit.getContent()))
                .collect(Collectors.toList());
    }

    private PostDocument toDocument(Post post) {
        String id = post.getId() == null ? null : String.valueOf(post.getId());
        return new PostDocument(id, post.getContent(), post.getAuthor(), post.getCreatedAt());
    }

    private Post toPost(PostDocument document) {
        Long id = document.getId() == null ? null : Long.valueOf(document.getId());
        return new Post(id, document.getContent(), document.getAuthor(), document.getCreatedAt());
    }
}
