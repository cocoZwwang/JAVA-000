package pers.cocoade.learning.dubbo.a.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import pers.cocoade.learning.dubbo.api.domain.RMBAccount;

import java.util.Date;

@Mapper
public interface RMBAccountMapper {

    @Update("update rmb_account set balance = #{balance},update_time = now() where id = #{id}")
    int updateBalance(@Param("id") Long id,@Param("balance") Long balance);

    @Update("update rmb_account set state = #{state},update_time = now() where id = #{id}")
    int updateState(@Param("id") Long id,@Param("state") Integer state);

    @Insert("insert into rmb_account (id,name,balance,state,create_time,update_time) " +
            "values (#{id},#{name},#{balance},#{state},#{createTime},#{updateTime})")
    int add(RMBAccount account);

    @Delete("delete from rmb_account where id = #{id}")
    int delete(@Param("id") Long id);

    @Select("select * from rmb_account where id = #{id}")
    @Results(id = "RMBAccountMap",value = {
            @Result(column = "id",property = "id",id = true,javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "name",property = "name",javaType = String.class,jdbcType = JdbcType.VARCHAR),
            @Result(column = "balance",property = "balance",javaType = Long.class,jdbcType = JdbcType.BIGINT),
            @Result(column = "state",property = "state",javaType = Integer.class,jdbcType = JdbcType.SMALLINT),
            @Result(column = "create_time",property = "createTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP),
            @Result(column = "update_time",property = "updateTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP)
    })
    RMBAccount selectOne(@Param("id") Long id);
}
