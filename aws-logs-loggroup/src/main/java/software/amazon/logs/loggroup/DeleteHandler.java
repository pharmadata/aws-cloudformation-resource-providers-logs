package software.amazon.logs.loggroup;

import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import java.util.Objects;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();
        final ProxyClient<CloudWatchLogsClient> cwl = ClientBuilder.getClient(proxy);
        try {
            proxy.injectCredentialsAndInvokeV2(Translator.translateToDeleteRequest(model),
                cwl.client()::deleteLogGroup);
        } catch (final ResourceNotFoundException e) {
            throw new software.amazon.cloudformation.exceptions.ResourceNotFoundException(ResourceModel.TYPE_NAME,
                Objects.toString(model.getPrimaryIdentifier()));
        }

        final String message = String.format("%s [%s] successfully deleted.",
                ResourceModel.TYPE_NAME, model.getLogGroupName());
        logger.log(message);
        return ProgressEvent.defaultSuccessHandler(null);
    }
}
