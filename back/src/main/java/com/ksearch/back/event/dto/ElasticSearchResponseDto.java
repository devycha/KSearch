package com.ksearch.back.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ElasticSearchResponseDto {
    public int took;
    public boolean timed_out;
    public Shards _shards;
    public Hits hits;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Shards {
        public int total;
        public int successful;
        public int skipped;
        public int failed;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Hits {
        public Total total;
        public double max_score;
        public List<Hit> hits;

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Total {
            public int value;
            public String relation;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Hit {
            public String _index;
            public String _type;
            public String _id;
            public double _score;
            public Source _source;

            @Getter
            @Setter
            @NoArgsConstructor
            public static class Source {
                public int id;
                public String name;
            }
        }
    }
}


/*
response data sample
{
    "took": 10,
    "timed_out": false,
    "_shards": {
        "total": 1,
        "successful": 1,
        "skipped": 0,
        "failed": 0
    },
    "hits": {
        "total": {
            "value": 4,
            "relation": "eq"
        },
        "max_score": 0.67283857,
        "hits": [
            {
                "_index": "ac_test",
                "_type": "_doc",
                "_id": "2",
                "_score": 0.67283857,
                "_source": {
                    "@timestamp": "2023-04-11T06:48:00.881821Z",
                    "id": 2,
                    "name": "테스트용이벤트",
                    "@version": "1"
                }
            },
            {
                "_index": "ac_test",
                "_type": "_doc",
                "_id": "3",
                "_score": 0.67283857,
                "_source": {
                    "@timestamp": "2023-04-11T06:48:00.881969Z",
                    "id": 3,
                    "name": "테스트2",
                    "@version": "1"
                }
            },
            {
                "_index": "ac_test",
                "_type": "_doc",
                "_id": "5",
                "_score": 0.67283857,
                "_source": {
                    "@timestamp": "2023-04-11T06:48:00.882113Z",
                    "id": 5,
                    "name": "테스트5",
                    "@version": "1"
                }
            },
            {
                "_index": "ac_test",
                "_type": "_doc",
                "_id": "4",
                "_score": 0.67283857,
                "_source": {
                    "@timestamp": "2023-04-11T06:48:00.961828Z",
                    "id": 4,
                    "name": "테스트3",
                    "@version": "1"
                }
            }
        ]
    }
}
 */