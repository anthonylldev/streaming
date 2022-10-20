package com.anthonylldev.streaming.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.anthonylldev.streaming.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EpisodeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EpisodeDTO.class);
        EpisodeDTO episodeDTO1 = new EpisodeDTO();
        episodeDTO1.setId(1L);
        EpisodeDTO episodeDTO2 = new EpisodeDTO();
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
        episodeDTO2.setId(episodeDTO1.getId());
        assertThat(episodeDTO1).isEqualTo(episodeDTO2);
        episodeDTO2.setId(2L);
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
        episodeDTO1.setId(null);
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
    }
}
