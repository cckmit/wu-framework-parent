package com.wu.framework.easy.excel.web;

import com.wu.framework.easy.excel.stereotype.RequestExcelBody;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.multipart.support.MultipartResolutionDelegate;
import org.springframework.web.multipart.support.RequestPartServletServerHttpRequest;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * description excel 导入拦截  未完成
 * the method has parameter with annotation RequestExcelBody will auto  converter to List
 * see {@link RequestExcelBody}
 *
 * @author Jia wei Wu
 * @date 2021/4/23 上午10:13
 */
@Deprecated
public class RequestExcelBodyHandlerMethodArgumentResolver extends AbstractMessageConverterMethodArgumentResolver {


    /**
     * Basic constructor with converters only.
     *
     * @param converters
     */
    public RequestExcelBodyHandlerMethodArgumentResolver(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    /**
     * Constructor with converters and {@code Request~} and {@code ResponseBodyAdvice}.
     *
     * @param converters
     * @param requestResponseBodyAdvice
     * @since 4.2
     */
    public RequestExcelBodyHandlerMethodArgumentResolver(List<HttpMessageConverter<?>> converters, List<Object> requestResponseBodyAdvice) {
        super(converters, requestResponseBodyAdvice);
    }

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestExcelBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        Assert.state(servletRequest != null, "No HttpServletRequest");

        RequestExcelBody requestPart = parameter.getParameterAnnotation(RequestExcelBody.class);
        boolean isRequired = ((requestPart == null || requestPart.required()) && !parameter.isOptional());

        String name = getPartName(parameter, requestPart);
        parameter = parameter.nestedIfOptional();
        Object arg = null;

        Object mpArg = MultipartResolutionDelegate.resolveMultipartArgument(name, parameter, servletRequest);
        if (mpArg != MultipartResolutionDelegate.UNRESOLVABLE) {
            arg = mpArg;
        } else {
            try {
                HttpInputMessage inputMessage = new RequestPartServletServerHttpRequest(servletRequest, name);
//                arg = readWithMessageConverters(inputMessage, parameter, parameter.getNestedGenericParameterType());
                arg = readWithMessageConverters(inputMessage, parameter, MultipartFile.class);
                if (binderFactory != null) {
                    WebDataBinder binder = binderFactory.createBinder(request, arg, name);
                    if (arg != null) {
                        validateIfApplicable(binder, parameter);
                        if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                            throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
                        }
                    }
                    if (mavContainer != null) {
                        mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + name, binder.getBindingResult());
                    }
                }
            } catch (MissingServletRequestPartException | MultipartException ex) {
                if (isRequired) {
                    throw ex;
                }
            }
        }

        if (arg == null && isRequired) {
            if (!MultipartResolutionDelegate.isMultipartRequest(servletRequest)) {
                throw new MultipartException("Current request is not a multipart request");
            } else {
                throw new MissingServletRequestPartException(name);
            }
        }
        adaptArgumentIfNecessary(arg, parameter);

        return adaptArgumentIfNecessary(arg, parameter);
    }

    private String getPartName(MethodParameter methodParam, @Nullable RequestExcelBody requestPart) {
        String partName = (requestPart != null ? requestPart.name() : "");
        if (partName.isEmpty()) {
            partName = methodParam.getParameterName();
            if (partName == null) {
                throw new IllegalArgumentException("Request part name for argument type [" +
                        methodParam.getNestedParameterType().getName() +
                        "] not specified, and parameter name information not found in class file either.");
            }
        }
        return partName;
    }
}