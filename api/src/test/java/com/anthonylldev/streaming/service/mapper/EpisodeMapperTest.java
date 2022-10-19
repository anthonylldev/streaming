package com.anthonylldev.streaming.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EpisodeMapperTest {

    private EpisodeMapper episodeMapper;

    @BeforeEach
    public void setUp() {
        episodeMapper = new EpisodeMapperImpl();
    }
}
