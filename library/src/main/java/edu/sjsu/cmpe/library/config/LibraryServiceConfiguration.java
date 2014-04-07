package edu.sjsu.cmpe.library.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class LibraryServiceConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private String stompQueueName;

    @NotEmpty
    @JsonProperty
    private String stompTopicName;

    @NotEmpty
    @JsonProperty
    private String libraryName;

    @NotEmpty
    @JsonProperty
    private String apolloUser;

    @NotEmpty
    @JsonProperty
    private String apolloPassword;

    @NotEmpty
    @JsonProperty
    private String apolloHost;

    @NotEmpty
    @JsonProperty
    private String apolloPort;

}
