package com.example.myapp;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

public class S3TestIT {

    DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.14.1");

    @Rule
    public LocalStackContainer localstack = new LocalStackContainer(localstackImage)
            .withServices(S3);

    @Test
    public void createS3Object() {

        // Crete bucket
        S3Client s3 = S3Client
                .builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        localstack.getAccessKey(), localstack.getSecretKey()
                )))
                .region(Region.of(localstack.getRegion()))
                .build();
        s3.createBucket(b -> b.bucket("bucket"));

        // Write object
        s3.putObject(b -> b.bucket("bucket").key("object"), RequestBody.fromBytes("content".getBytes()));

        // Read object
        ResponseInputStream<GetObjectResponse> object = s3.getObject(GetObjectRequest.builder()
                .bucket("bucket")
                .key("object")
                .build());
        try {
            String text = new String(object.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(text).isEqualTo("content");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
