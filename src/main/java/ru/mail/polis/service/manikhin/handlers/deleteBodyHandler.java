package ru.mail.polis.service.manikhin.handlers;

import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.concurrent.RejectedExecutionException;

public final class deleteBodyHandler implements HttpResponse.BodyHandler<Void> {

    public static final HttpResponse.BodyHandler<Void> INSTANCE = new deleteBodyHandler();

    private deleteBodyHandler() {
    }

    @Override
    public HttpResponse.BodySubscriber<Void> apply(
            @NotNull final HttpResponse.ResponseInfo responseInfo) {
        final int status = responseInfo.statusCode();
        if (status == 202) {
            return HttpResponse.BodySubscribers.discarding();
        } else {
            throw new RejectedExecutionException("Can't get response");
        }
    }
}