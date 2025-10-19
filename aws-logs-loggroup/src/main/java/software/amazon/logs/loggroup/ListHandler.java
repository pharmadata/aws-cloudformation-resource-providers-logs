package software.amazon.logs.loggroup;

import software.amazon.awssdk.services.cloudwatchlogs.model.ListTagsLogGroupResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.LogGroup;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsResponse;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import java.util.Map;
import java.util.stream.Collectors;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ProxyClient<CloudWatchLogsClient> cwl = ClientBuilder.getClient(proxy);
        final DescribeLogGroupsResponse response =
                proxy.injectCredentialsAndInvokeV2(Translator.translateToListRequest(request.getNextToken()),
                    cwl.client()::describeLogGroups);

        final Map<String, ListTagsLogGroupResponse> tagResponses = Translator.streamOfOrEmpty(response.logGroups())
                .collect(Collectors.toMap(
                        LogGroup::logGroupName,
                        logGroup -> proxy.injectCredentialsAndInvokeV2(Translator.translateToListTagsLogGroupRequest(logGroup.logGroupName()),
                                cwl.client()::listTagsLogGroup))
                );

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .status(OperationStatus.SUCCESS)
                .resourceModels(Translator.translateForList(response, tagResponses))
                .nextToken(response.nextToken())
                .build();
    }
}
