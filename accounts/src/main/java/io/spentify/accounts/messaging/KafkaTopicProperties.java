package io.spentify.accounts.messaging;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public final class KafkaTopicProperties {

    private String name;
    private Short replicas;
    private Integer partitions;
    private final Map<String, String> configs = new HashMap<>();

}
