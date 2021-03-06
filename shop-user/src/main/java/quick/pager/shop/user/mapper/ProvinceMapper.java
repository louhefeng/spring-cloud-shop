package quick.pager.shop.user.mapper;

import java.util.List;
import quick.pager.shop.model.user.Province;

public interface ProvinceMapper {

    int insertSelective(Province record);

    Province selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Province record);

    List<Province> selectAll();

}