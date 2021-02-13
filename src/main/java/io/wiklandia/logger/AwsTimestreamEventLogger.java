package io.wiklandia.logger;


import ch.qos.logback.classic.spi.ILoggingEvent;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import software.amazon.awssdk.services.timestreamwrite.model.ConflictException;
import software.amazon.awssdk.services.timestreamwrite.model.CreateDatabaseRequest;
import software.amazon.awssdk.services.timestreamwrite.model.CreateTableRequest;

import java.time.Duration;

public class AwsTimestreamEventLogger {

    private final AwsSettings awsSettings;
    private final TimestreamWriteClient timestreamWriteClient;


    public static AwsTimestreamEventLogger create(AwsSettings awsSettings) {
        System.out.println("Creating AWS client using: " + awsSettings);
        return new AwsTimestreamEventLogger(awsSettings);
    }

    public void writeLog(ILoggingEvent event) {
        System.out.println("logging to aws " + event);
    }

    private AwsTimestreamEventLogger(AwsSettings awsSettings) {
        this.awsSettings = awsSettings;
        this.timestreamWriteClient = buildWriteClient();
//        createDatabase();
//        createTable();
    }

    private TimestreamWriteClient buildWriteClient() {
        ApacheHttpClient.Builder httpClientBuilder =
                ApacheHttpClient.builder();
        httpClientBuilder.maxConnections(awsSettings.getMaxConnections());

        RetryPolicy.Builder retryPolicy =
                RetryPolicy.builder();
        retryPolicy.numRetries(awsSettings.getNumRetries());

        ClientOverrideConfiguration.Builder overrideConfig =
                ClientOverrideConfiguration.builder();
        overrideConfig.apiCallAttemptTimeout(Duration.ofSeconds(awsSettings.getTimeoutInSeconds()));
        overrideConfig.retryPolicy(retryPolicy.build());

        return TimestreamWriteClient.builder()
                .httpClientBuilder(httpClientBuilder)
                .overrideConfiguration(overrideConfig.build())
                .region(Region.of(awsSettings.getRegion()))
                .build();
    }

    private void createDatabase() {
        System.out.println("Creating database");
        final CreateDatabaseRequest request = CreateDatabaseRequest.builder().databaseName(awsSettings.getDatabaseName()).build();
        try {
            timestreamWriteClient.createDatabase(request);
            System.out.println("Database created successfully");
        } catch (ConflictException e) {
            System.out.println("Database exists. Skipping database creation");
        }
    }

    private void createTable() {
        System.out.println("Creating table");
        final CreateTableRequest createTableRequest = CreateTableRequest.builder()
                .databaseName(awsSettings.getDatabaseName()).tableName(awsSettings.getTableName()).build();
        try {
            timestreamWriteClient.createTable(createTableRequest);
            System.out.println("Table successfully created.");
        } catch (ConflictException e) {
            System.out.println("Table exists on database [" + awsSettings.getDatabaseName() + "] . Skipping database creation");
        }
    }



}
