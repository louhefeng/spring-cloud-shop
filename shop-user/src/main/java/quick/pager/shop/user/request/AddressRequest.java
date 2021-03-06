package quick.pager.shop.user.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import quick.pager.common.request.Request;

@EqualsAndHashCode(callSuper = true)
@Data
public class AddressRequest extends Request {
    private static final long serialVersionUID = 480728885297418424L;

    private Long userId;
    /**
     * list : 地址列表<br />
     * modify : 修改地址<br />
     * 如果为空，则接口默认查询是列表
     */
    private String event;

    /**
     * 用户手机号码
     */
    private String phone;
    /**
     * 收货人姓名
     */
    private String username;
    /**
     * 所在地区
     */
    private String area;
    /**
     * 详细地址
     */
    private String detailAddress;
    /**
     * 标签
     */
    private String label;
    /**
     * 是否默认地址标志
     * true：默认地址
     * false：不是默认地址
     */
    private Boolean defaultAddress;
}
