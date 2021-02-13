package io.wiklandia.logger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AwsSettings {
    private int maxConnections = 5000;
    private int numRetries = 10;
    private int timeoutInSeconds = 20;
    private String region = "us-east-1";
    private String databaseName = "casualty-default-database";
    private String tableName = "casualty-default-table";
}
