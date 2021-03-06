package quick.pager.shop.zuul.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import java.util.LinkedHashMap;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import quick.pager.common.constants.ResponseStatus;
import quick.pager.common.response.Response;
import quick.pager.common.service.RedisService;
import quick.pager.common.utils.PermissionMap;
import quick.pager.shop.zuul.properties.PermissionProperties;

/**
 * 登陆验证filter实现
 *
 * @author siguiyang
 */
@Component
@Slf4j
public class LoginFilter extends ZuulFilter {

    @Autowired
    private PermissionProperties permission;
    @Autowired
    private RedisService redisService;

    @Autowired
    private RouteLocator routeLocator;

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String requestURI = request.getRequestURI();
        // 如果请求的资源路径在白名单内，则不走登陆的过滤器
        return !permission.getPermissions().containsValue(requestURI);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String token = request.getParameter("token");
        String sysCode = request.getParameter("sysCode");
        String userId = request.getParameter("userId");

        // 如果请求域中没有userId入参，则检测restful 风控中是否存在userId
        if (StringUtils.isEmpty(userId)) {
            NativeWebRequest webRequest = new ServletWebRequest(request);
            LinkedHashMap map = (LinkedHashMap) webRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (!CollectionUtils.isEmpty(map)) {
                userId = (String) map.get("userId");
            }
        }

//        Optional<Route> optional = routeLocator.getRoutes().stream().filter(route -> "shop-manage".equals(route.getLocation())).findFirst();
        String requestURI = urlPathHelper.getPathWithinApplication(request);
        Route requestRoute = routeLocator.getMatchingRoute(requestURI);

        if ("shop-manage".equals(requestRoute.getLocation())) {
            log.info("进入管理后台系统路由");

            // 不在白名单，并且登陆状态，没有访问权限资源进入
            if (!permission.getPermissions().containsValue(requestURI)
                    && !StringUtils.isEmpty(sysCode)
                    && !redisService.contains(sysCode, requestURI)) {
                log.info("您没有权限操作此功能。。。");
                writeResponse(context, ResponseStatus.Code.NO_PERMISSION, ResponseStatus.NO_PERMISSION);
            }
            return null;
        }

        String tokenFromRedis = String.valueOf(redisService.getValueOps(userId));
        // redis 中没有用户登陆的token ，则未登陆
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(tokenFromRedis)) {

            writeResponse(context, ResponseStatus.Code.LOGIN_EXPIRE, ResponseStatus.LOGIN_EXPIRE);
            return null;
        }

        // token 不一致, 则token失效
        if (!token.equalsIgnoreCase(tokenFromRedis)) {
            writeResponse(context, ResponseStatus.Code.LOGIN_EXPIRE, ResponseStatus.LOGIN_EXPIRE);
            return null;
        }
        return null;
    }


    /**
     * 返回客户端结果
     */
    private void writeResponse(RequestContext context, int code, String responseMsg) {

        context.setSendZuulResponse(false);
        context.setResponseStatusCode(HttpStatus.OK.value());
        Response<String> noPermission = new Response<>();
        noPermission.setCode(code);
        noPermission.setMsg(responseMsg);
        context.setResponseBody(JSON.toJSONString(noPermission));
    }
}
