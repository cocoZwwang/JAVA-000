package pers.cocoade.learning.dubbo.b.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;
import pers.cocoade.learning.dubbo.api.domain.DollarAccount;

import java.util.Date;

@Mapper
public interface DollarAccountMapper {
    @Update("update dollar_account set balance = #{balance},update_time = now() where id = #{id}")
    int updateBalance(@Param("id") Long id, @Param("balance") Long balance);

    @Update("update dollar_account set state = #{state},update_time = now() where id = #{id}")
    int updateState(@Param("id") Long id,@Param("state") Integer state);

    @Insert("insert into dollar_account (id,name,balance,state,create_time,update_time) " +
            "values (#{id},#{name},#{balance},#{state},#{createTime},#{updateTime})")
    int add(DollarAccount account);

    @Delete("delete from dollar_account where id = #{id}")
    int delete(@Param("id") Long id);

    @Select("select * from dollar_account where id = #{id}")
    @Results(id = "DollarAccountMap",value = {
        @Result(column = "id",property = "id",id = true,javaType = Long.class,jdbcType = JdbcType.BIGINT),
        @Result(column = "name",property = "name",javaType = String.class,jdbcType = JdbcType.VARCHAR),
        @Result(column = "balance",property = "balance",javaType = Long.class,jdbcType = JdbcType.BIGINT),
        @Result(column = "state",property = "state",javaType = Integer.class,jdbcType = JdbcType.SMALLINT),
        @Result(column = "create_time",property = "createTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP),
        @Result(column = "update_time",property = "updateTime",javaType = Date.class,jdbcType = JdbcType.TIMESTAMP),
    })
    DollarAccount selectOne(@Param("id") Long id);
}
