package quick.pager.shop.goods.request;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import quick.pager.common.request.Request;

/**
 * 购物车请求对象
 *
 * @author siguiyang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CartRequest extends Request {
    private static final long serialVersionUID = 4210136695507304701L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 商品id集合
     */
    private List<Long> goodsIds;

}
