package quick.pager.shop.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import quick.pager.common.response.Response;
import quick.pager.shop.model.activity.DiscountCoupon;
import quick.pager.shop.order.fallback.ActivityClientFallbackFactory;

/**
 * activity 模块
 *
 * @author siguiyang
 */
@FeignClient(value = "shop-activity", path = "/activity", fallbackFactory = ActivityClientFallbackFactory.class)
public interface ActivityClient {

    /**
     * 获取一张优惠券详情
     */
    @RequestMapping(value = "/userCoupons/{couponId}", method = RequestMethod.POST)
    Response<DiscountCoupon> userCoupons(@PathVariable("couponId") Long couponId);
}
