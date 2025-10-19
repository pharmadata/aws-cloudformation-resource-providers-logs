package software.amazon.logs.loggroup;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.retry.conditions.RetryCondition;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.cloudformation.LambdaWrapper;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.awssdk.regions.Region;

public class ClientBuilder {
    private ClientBuilder() {}

    private static final RetryPolicy RETRY_POLICY =
        RetryPolicy.builder()
            .numRetries(6)
            .retryCondition(RetryCondition.defaultRetryCondition())
            .build();

    public static ProxyClient<CloudWatchLogsClient> getClient(final AmazonWebServicesClientProxy proxy) {
        return proxy.newProxy(() -> CloudWatchLogsClient.builder()
            .httpClient(LambdaWrapper.HTTP_CLIENT)
            .overrideConfiguration(c -> c.retryPolicy(RETRY_POLICY))
            .build());
    }
}
