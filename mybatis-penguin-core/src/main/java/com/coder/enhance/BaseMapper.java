package com.coder.enhance;

import com.code.enhance.PageModel;
import com.coder.enhance.plugin.Pager;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.Collection;
import java.util.List;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
public interface BaseMapper<Entity, Primary> {
    /**
     * 通过主键查询
     *
     * @param id
     * @return
     */
    Entity loadOne(@Param("PrimaryKey") Primary id);

    /**
     * 通过主键批量查询
     *
     * @param ids
     * @return
     */
    List<Entity> findByIds(@Param("PrimaryKey") Collection<Primary> ids);

    /**
     * 查询所有
     *
     * @return
     */
    List<Entity> findAll();


    /**
     * 分页查询
     *
     * @param pager
     * @return
     */
    PageModel<Entity> findByPage(@Param("pager") Pager pager);

    /**
     * 分页查询，不需要总条数
     *
     * @param rowBounds
     * @return
     */
    List<Entity> findLimit(RowBounds rowBounds);

    /**
     * 插入
     *
     * @param entity
     * @return
     */
    int insert(Entity entity);

    /**
     * 批量插入
     *
     * @param entities
     * @return
     */
    int insertAll(Collection<? extends Entity> entities);

    /**
     * 更新数据，只更新不为null的属性
     *
     * @param entity
     * @return
     */
    int updateSelective(Entity entity);

    /**
     * 更新所有字段
     *
     * @param entity
     * @return
     */
    int update(Entity entity);

    /**
     * 批量更新，只更新不为null的属性
     *
     * @param entities
     * @return
     */
    int updateSelectiveAll(Collection<? extends Entity> entities);

    /**
     * 批量更新
     *
     * @param entities
     * @return
     */
    int updateAll(List<? extends Entity> entities);

    /**
     * 根据主键删除
     *
     * @param id
     * @return
     */
    int deleteOne(@Param("PrimaryKey") Primary id);

    /**
     * 根据主键批量删除
     *
     * @param ids
     * @return
     */
    int deleteAll(@Param("PrimaryKey") Collection<Primary> ids);
}
