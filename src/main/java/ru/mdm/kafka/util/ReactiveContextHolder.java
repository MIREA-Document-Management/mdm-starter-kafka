package ru.mdm.kafka.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReactiveContextHolder {

    private static final String UNKNOWN = "Не определено";
    private static final Pattern NOT_IP_SYMBOLS = Pattern.compile("[^A-Za-z0-9:./]");
    private static final List<String> IP_HEADER_NAMES = List.of(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    );

    public static Mono<String> getUserName() {
        return Mono.just("user");
//        return ReactiveSecurityContextHolder.getContext() TODO раскомментировать после добавления авторизации
//                .map(securityContext -> {
//                    var name = UNKNOWN;
//                    if (securityContext != null && securityContext.getAuthentication() != null) {
//                        name = securityContext.getAuthentication().getName();
//                    }
//                    return name;
//                })
//                .defaultIfEmpty(UNKNOWN);
    }

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(Mono::just)
                .flatMap(ctx -> {
                    if(ctx.hasKey(ServerWebExchange.class)){
                        return Mono.just(ctx.get(ServerWebExchange.class).getRequest());
                    }else{
                        return Mono.empty();
                    }
                });
    }

    public static Mono<String> getRequestId() {
        return getRequest().map(ServerHttpRequest::getId)
                .switchIfEmpty(Mono.just(UNKNOWN))
                .onErrorReturn(UNKNOWN);
    }

    public static Mono<String> getRemoteIp() {
        return Flux.fromIterable(IP_HEADER_NAMES)
                .flatMap(ip -> Mono.just(ip)
                        .zipWith(getRequest()))
                .flatMap(headerRequestTuple ->
                        sanitizeIp(headerRequestTuple.getT2()
                                .getHeaders().getFirst(headerRequestTuple.getT1())))
                .filter(headerValue -> StringUtils.hasText(headerValue) && !"unknown".equalsIgnoreCase(headerValue))
                .map(addresses -> addresses.split(",")[0])
                .next()
                .switchIfEmpty(getRemoteAddress())
                .doOnError(throwable -> log.warn("getRemoteIp: Can not retrieve ip address", throwable))
                .onErrorReturn(UNKNOWN);
    }

    private static Mono<String> getRemoteAddress() {
        return getRequest()
                .flatMap(request -> {
                    if (request.getRemoteAddress() == null) {
                        return getInetAddressMono(request);
                    }
                    String remoteAddress = request.getRemoteAddress().getAddress().getHostAddress();
                    if (remoteAddress == null || "0:0:0:0:0:0:0:1".equals(remoteAddress)) {
                        return getInetAddressMono(request);
                    }
                    return Mono.just(remoteAddress);
                })
                .switchIfEmpty(Mono.just(UNKNOWN))
                .onErrorReturn(UNKNOWN);
    }

    private static Mono<String> getInetAddressMono(ServerHttpRequest request) {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            return Mono.just(hostAddress);
        } catch (UnknownHostException e) {
            log.debug("getRemoteAddress: Host not found for request = {}", request, e);
            return Mono.just(UNKNOWN);
        }
    }

    private static Mono<String> sanitizeIp(String ip) {
        if (ip == null) {
            return Mono.empty();
        }
        return Mono.just(ip.replaceAll(NOT_IP_SYMBOLS.pattern(), ""));
    }
}
